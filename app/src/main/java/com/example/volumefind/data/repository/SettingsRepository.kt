package com.example.volumefind.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.volumefind.domain.model.LengthUnit
import com.example.volumefind.domain.model.VolumeShape
import com.example.volumefind.domain.model.VolumeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.math.RoundingMode

data class AppSettings(
    val themeMode: String = THEME_SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val decimalPlaces: Int = 6,
    val roundingMode: RoundingMode = RoundingMode.HALF_UP,
    val showFormula: Boolean = true,
    val showCalculationSteps: Boolean = true,
    val showShapeDiagram: Boolean = true,
    val confirmHistoryDelete: Boolean = true,
    val preserveLastCalculation: Boolean = true
) {
    companion object {
        const val THEME_SYSTEM = "System"
        const val THEME_LIGHT = "Light"
        const val THEME_DARK = "Dark"
    }
}

class SettingsRepository(
    private val prefs: SharedPreferences
) {
    constructor(context: Context) : this(
        context.getSharedPreferences("volume_find_settings", Context.MODE_PRIVATE)
    )

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<AppSettings> = _settingsFlow.asStateFlow()

    private fun loadSettings(): AppSettings {
        val theme = prefs.getString(KEY_THEME, AppSettings.THEME_SYSTEM) ?: AppSettings.THEME_SYSTEM
        val dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
        val haptic = prefs.getBoolean(KEY_HAPTIC, true)
        val animations = prefs.getBoolean(KEY_ANIMATIONS, true)
        val decimals = prefs.getInt(KEY_DECIMALS, 6)
        val roundingStr = prefs.getString(KEY_ROUNDING, RoundingMode.HALF_UP.name) ?: RoundingMode.HALF_UP.name
        val rounding = try {
            RoundingMode.valueOf(roundingStr)
        } catch (_: Exception) {
            RoundingMode.HALF_UP
        }
        val showFormula = prefs.getBoolean(KEY_SHOW_FORMULA, true)
        val showSteps = prefs.getBoolean(KEY_SHOW_STEPS, true)
        val showDiagram = prefs.getBoolean(KEY_SHOW_DIAGRAM, true)
        val confirmDelete = prefs.getBoolean(KEY_CONFIRM_DELETE, true)
        val preserve = prefs.getBoolean(KEY_PRESERVE_LAST, true)

        return AppSettings(
            themeMode = theme,
            dynamicColor = dynamicColor,
            hapticFeedback = haptic,
            animations = animations,
            decimalPlaces = decimals,
            roundingMode = rounding,
            showFormula = showFormula,
            showCalculationSteps = showSteps,
            showShapeDiagram = showDiagram,
            confirmHistoryDelete = confirmDelete,
            preserveLastCalculation = preserve
        )
    }

    fun updateTheme(theme: String) {
        prefs.edit().putString(KEY_THEME, theme).apply()
        _settingsFlow.value = _settingsFlow.value.copy(themeMode = theme)
    }

    fun updateDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(dynamicColor = enabled)
    }

    fun updateHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(hapticFeedback = enabled)
    }

    fun updateAnimations(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(animations = enabled)
    }

    fun updateDecimalPlaces(places: Int) {
        prefs.edit().putInt(KEY_DECIMALS, places).apply()
        _settingsFlow.value = _settingsFlow.value.copy(decimalPlaces = places)
    }

    fun updateRoundingMode(mode: RoundingMode) {
        prefs.edit().putString(KEY_ROUNDING, mode.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(roundingMode = mode)
    }

    fun updateShowFormula(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_FORMULA, show).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showFormula = show)
    }

    fun updateShowCalculationSteps(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_STEPS, show).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showCalculationSteps = show)
    }

    fun updateShowShapeDiagram(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_DIAGRAM, show).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showShapeDiagram = show)
    }

    fun updateConfirmHistoryDelete(confirm: Boolean) {
        prefs.edit().putBoolean(KEY_CONFIRM_DELETE, confirm).apply()
        _settingsFlow.value = _settingsFlow.value.copy(confirmHistoryDelete = confirm)
    }

    fun updatePreserveLastCalculation(preserve: Boolean) {
        prefs.edit().putBoolean(KEY_PRESERVE_LAST, preserve).apply()
        _settingsFlow.value = _settingsFlow.value.copy(preserveLastCalculation = preserve)
    }

    fun resetSettings() {
        prefs.edit().clear().apply()
        _settingsFlow.value = AppSettings()
    }

    // Save and load last state
    fun saveLastCalculationState(
        shape: VolumeShape,
        inputs: Map<String, String>,
        inputUnits: Map<String, LengthUnit>,
        resultUnit: VolumeUnit
    ) {
        if (!_settingsFlow.value.preserveLastCalculation) return

        val jsonInputs = JSONObject(inputs)
        val jsonUnits = JSONObject()
        inputUnits.forEach { (k, v) -> jsonUnits.put(k, v.name) }

        prefs.edit()
            .putString(KEY_LAST_SHAPE, shape.name)
            .putString(KEY_LAST_INPUTS, jsonInputs.toString())
            .putString(KEY_LAST_UNITS, jsonUnits.toString())
            .putString(KEY_LAST_RESULT_UNIT, resultUnit.name)
            .apply()
    }

    fun loadLastCalculationState(): LastCalculationData? {
        if (!_settingsFlow.value.preserveLastCalculation) return null
        val shapeName = prefs.getString(KEY_LAST_SHAPE, null) ?: return null
        val shape = try {
            VolumeShape.valueOf(shapeName)
        } catch (_: Exception) {
            VolumeShape.CUBE
        }

        val inputsRaw = prefs.getString(KEY_LAST_INPUTS, null)
        val inputs = mutableMapOf<String, String>()
        if (!inputsRaw.isNullOrEmpty()) {
            val json = JSONObject(inputsRaw)
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                inputs[k] = json.getString(k)
            }
        }

        val unitsRaw = prefs.getString(KEY_LAST_UNITS, null)
        val units = mutableMapOf<String, LengthUnit>()
        if (!unitsRaw.isNullOrEmpty()) {
            val json = JSONObject(unitsRaw)
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val unitName = json.getString(k)
                try {
                    units[k] = LengthUnit.valueOf(unitName)
                } catch (_: Exception) {
                    units[k] = LengthUnit.CENTIMETER
                }
            }
        }

        val resultUnitName = prefs.getString(KEY_LAST_RESULT_UNIT, VolumeUnit.CUBIC_CENTIMETER.name)
        val resultUnit = try {
            VolumeUnit.valueOf(resultUnitName ?: VolumeUnit.CUBIC_CENTIMETER.name)
        } catch (_: Exception) {
            VolumeUnit.CUBIC_CENTIMETER
        }

        return LastCalculationData(shape, inputs, units, resultUnit)
    }

    data class LastCalculationData(
        val shape: VolumeShape,
        val inputs: Map<String, String>,
        val inputUnits: Map<String, LengthUnit>,
        val resultUnit: VolumeUnit
    )

    companion object {
        private const val KEY_THEME = "theme_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_HAPTIC = "haptic_feedback"
        private const val KEY_ANIMATIONS = "animations"
        private const val KEY_DECIMALS = "decimal_places"
        private const val KEY_ROUNDING = "rounding_mode"
        private const val KEY_SHOW_FORMULA = "show_formula"
        private const val KEY_SHOW_STEPS = "show_calculation_steps"
        private const val KEY_SHOW_DIAGRAM = "show_shape_diagram"
        private const val KEY_CONFIRM_DELETE = "confirm_history_delete"
        private const val KEY_PRESERVE_LAST = "preserve_last_calculation"

        private const val KEY_LAST_SHAPE = "last_shape"
        private const val KEY_LAST_INPUTS = "last_inputs"
        private const val KEY_LAST_UNITS = "last_units"
        private const val KEY_LAST_RESULT_UNIT = "last_result_unit"
    }
}
