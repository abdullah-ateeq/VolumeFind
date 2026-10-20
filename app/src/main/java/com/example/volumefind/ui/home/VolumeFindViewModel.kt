package com.example.volumefind.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.volumefind.data.repository.AppSettings
import com.example.volumefind.data.repository.SettingsRepository
import com.example.volumefind.data.repository.VolumeFindHistoryRepository
import com.example.volumefind.domain.calculator.VolumeCalculator
import com.example.volumefind.domain.constants.MathConstants
import com.example.volumefind.domain.formatter.VolumeFindFormatter
import com.example.volumefind.domain.model.LengthUnit
import com.example.volumefind.domain.model.VolumeFindHistory
import com.example.volumefind.domain.model.VolumeResult
import com.example.volumefind.domain.model.VolumeShape
import com.example.volumefind.domain.model.VolumeUnit
import com.example.volumefind.domain.validator.VolumeFindValidator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode

sealed interface UserFeedbackEvent {
    data class ShowMessage(val message: String) : UserFeedbackEvent
    data object CalculationSaved : UserFeedbackEvent
    data object TextCopied : UserFeedbackEvent
}

class VolumeFindViewModel @JvmOverloads constructor(
    application: Application,
    private val settingsRepository: SettingsRepository = SettingsRepository(application),
    private val historyRepository: VolumeFindHistoryRepository = VolumeFindHistoryRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VolumeFindUiState())
    val uiState: StateFlow<VolumeFindUiState> = _uiState.asStateFlow()

    private val _feedbackEvents = MutableSharedFlow<UserFeedbackEvent>()
    val feedbackEvents: SharedFlow<UserFeedbackEvent> = _feedbackEvents.asSharedFlow()

    val settingsFlow: StateFlow<AppSettings> = settingsRepository.settingsFlow

    init {
        // Restore last calculation if configured
        val lastState = settingsRepository.loadLastCalculationState()
        if (lastState != null) {
            _uiState.update { current ->
                current.copy(
                    shape = lastState.shape,
                    inputs = lastState.inputs,
                    inputUnits = lastState.inputUnits,
                    resultUnit = lastState.resultUnit
                )
            }
        } else {
            // Default Cube
            val defaultShape = VolumeShape.CUBE
            _uiState.update { current ->
                current.copy(
                    shape = defaultShape,
                    inputs = defaultShape.defaultDimensions,
                    inputUnits = VolumeFindValidator.getRequiredDimensionKeys(defaultShape)
                        .associateWith { LengthUnit.CENTIMETER },
                    resultUnit = VolumeUnit.CUBIC_CENTIMETER
                )
            }
        }

        // Collect history and settings changes
        viewModelScope.launch {
            historyRepository.historyFlow.collect { historyList ->
                _uiState.update { it.copy(history = historyList) }
            }
        }

        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update { current ->
                    current.copy(
                        decimalPlaces = settings.decimalPlaces,
                        roundingMode = settings.roundingMode,
                        showFormula = settings.showFormula,
                        showCalculationSteps = settings.showCalculationSteps,
                        showShapeDiagram = settings.showShapeDiagram
                    )
                }
                recalculate()
            }
        }

        recalculate()
    }

    fun selectShape(newShape: VolumeShape) {
        if (_uiState.value.shape == newShape) return

        val initialInputs = newShape.defaultDimensions
        val initialUnits = VolumeFindValidator.getRequiredDimensionKeys(newShape)
            .associateWith { LengthUnit.CENTIMETER }

        _uiState.update { current ->
            current.copy(
                shape = newShape,
                inputs = initialInputs,
                inputUnits = initialUnits,
                errorMessage = null
            )
        }
        recalculate()
        persistCurrentState()
    }

    fun updateDimensionInput(dimensionKey: String, newValue: String) {
        val updatedInputs = _uiState.value.inputs.toMutableMap()
        updatedInputs[dimensionKey] = newValue

        _uiState.update { current ->
            current.copy(
                inputs = updatedInputs
            )
        }
        recalculate()
        persistCurrentState()
    }

    fun updateDimensionUnit(dimensionKey: String, newUnit: LengthUnit) {
        val updatedUnits = _uiState.value.inputUnits.toMutableMap()
        updatedUnits[dimensionKey] = newUnit

        _uiState.update { current ->
            current.copy(
                inputUnits = updatedUnits
            )
        }
        recalculate()
        persistCurrentState()
    }

    fun updateResultUnit(newResultUnit: VolumeUnit) {
        _uiState.update { current ->
            current.copy(
                resultUnit = newResultUnit
            )
        }
        recalculate()
        persistCurrentState()
    }

    fun recalculate() {
        val state = _uiState.value
        val (parsedInputs, validationError) = VolumeFindValidator.validateShapeInputs(
            shape = state.shape,
            inputs = state.inputs
        )

        if (validationError != null || parsedInputs == null) {
            _uiState.update {
                it.copy(
                    result = null,
                    errorMessage = validationError,
                    isCalculating = false
                )
            }
            return
        }

        try {
            val (volume, details) = VolumeCalculator.calculateShapeVolume(
                shape = state.shape,
                inputs = parsedInputs,
                inputUnits = state.inputUnits,
                targetUnit = state.resultUnit,
                mathContext = MathConstants.DEFAULT_MATH_CONTEXT
            )

            val volumeResult = VolumeResult(
                shape = state.shape,
                value = volume,
                outputUnit = state.resultUnit.toLengthUnit(),
                details = details,
                calculatedAt = System.currentTimeMillis()
            )

            _uiState.update {
                it.copy(
                    result = volumeResult,
                    errorMessage = null,
                    isCalculating = false
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    result = null,
                    errorMessage = e.message ?: "Calculation error",
                    isCalculating = false
                )
            }
        }
    }

    fun saveCurrentCalculation(): Boolean {
        val state = _uiState.value
        val res = state.result ?: return false

        val formattedResult = VolumeFindFormatter.formatVolume(
            value = res.value,
            unit = state.resultUnit,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )

        val saved = historyRepository.saveCalculation(
            shape = state.shape,
            inputs = state.inputs,
            inputUnits = state.inputUnits,
            resultValue = formattedResult,
            resultUnit = state.resultUnit
        )

        viewModelScope.launch {
            if (saved) {
                _feedbackEvents.emit(UserFeedbackEvent.CalculationSaved)
            } else {
                _feedbackEvents.emit(UserFeedbackEvent.ShowMessage("Calculation already saved"))
            }
        }

        return saved
    }

    fun restoreHistoryItem(historyItem: VolumeFindHistory) {
        _uiState.update { current ->
            current.copy(
                shape = historyItem.shape,
                inputs = historyItem.inputs,
                inputUnits = historyItem.inputUnits,
                resultUnit = historyItem.resultUnit,
                errorMessage = null
            )
        }
        recalculate()
        persistCurrentState()
        viewModelScope.launch {
            _feedbackEvents.emit(UserFeedbackEvent.ShowMessage("Restored ${historyItem.shape.displayName}"))
        }
    }

    fun deleteHistoryItem(id: String) {
        historyRepository.deleteItem(id)
    }

    fun clearAllHistory() {
        historyRepository.clearHistory()
        viewModelScope.launch {
            _feedbackEvents.emit(UserFeedbackEvent.ShowMessage("History cleared"))
        }
    }

    fun resetToDefaults() {
        val defaultShape = VolumeShape.CUBE
        val defaultInputs = defaultShape.defaultDimensions
        val defaultUnits = VolumeFindValidator.getRequiredDimensionKeys(defaultShape)
            .associateWith { LengthUnit.CENTIMETER }

        _uiState.update { current ->
            current.copy(
                shape = defaultShape,
                inputs = defaultInputs,
                inputUnits = defaultUnits,
                resultUnit = VolumeUnit.CUBIC_CENTIMETER,
                errorMessage = null
            )
        }
        recalculate()
        persistCurrentState()
        viewModelScope.launch {
            _feedbackEvents.emit(UserFeedbackEvent.ShowMessage("Reset to default"))
        }
    }

    // Settings actions
    fun setTheme(theme: String) = settingsRepository.updateTheme(theme)
    fun setDynamicColor(enabled: Boolean) = settingsRepository.updateDynamicColor(enabled)
    fun setHaptics(enabled: Boolean) = settingsRepository.updateHapticFeedback(enabled)
    fun setAnimations(enabled: Boolean) = settingsRepository.updateAnimations(enabled)
    fun setDecimalPlaces(places: Int) = settingsRepository.updateDecimalPlaces(places)
    fun setRoundingMode(mode: RoundingMode) = settingsRepository.updateRoundingMode(mode)
    fun setShowFormula(show: Boolean) = settingsRepository.updateShowFormula(show)
    fun setShowCalculationSteps(show: Boolean) = settingsRepository.updateShowCalculationSteps(show)
    fun setShowShapeDiagram(show: Boolean) = settingsRepository.updateShowShapeDiagram(show)
    fun setConfirmHistoryDelete(confirm: Boolean) = settingsRepository.updateConfirmHistoryDelete(confirm)
    fun setPreserveLastCalculation(preserve: Boolean) = settingsRepository.updatePreserveLastCalculation(preserve)
    fun resetSettings() = settingsRepository.resetSettings()

    private fun persistCurrentState() {
        val state = _uiState.value
        settingsRepository.saveLastCalculationState(
            shape = state.shape,
            inputs = state.inputs,
            inputUnits = state.inputUnits,
            resultUnit = state.resultUnit
        )
    }

    fun getCopyFormattedText(): String? {
        val state = _uiState.value
        val result = state.result ?: return null
        return VolumeFindFormatter.formatCopyTextWithInputs(
            result = result,
            inputs = state.inputs,
            inputUnits = state.inputUnits,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )
    }

    fun getCopyValueOnly(): String? {
        val state = _uiState.value
        val result = state.result ?: return null
        return VolumeFindFormatter.formatVolume(
            value = result.value,
            unit = state.resultUnit,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )
    }
}
