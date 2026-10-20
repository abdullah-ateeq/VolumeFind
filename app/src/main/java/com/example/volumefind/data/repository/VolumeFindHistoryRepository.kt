package com.example.volumefind.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.volumefind.domain.model.LengthUnit
import com.example.volumefind.domain.model.VolumeFindHistory
import com.example.volumefind.domain.model.VolumeShape
import com.example.volumefind.domain.model.VolumeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class VolumeFindHistoryRepository(
    private val prefs: SharedPreferences
) {
    constructor(context: Context) : this(
        context.getSharedPreferences("volume_find_history", Context.MODE_PRIVATE)
    )

    private val _historyFlow = MutableStateFlow<List<VolumeFindHistory>>(loadHistory())
    val historyFlow: StateFlow<List<VolumeFindHistory>> = _historyFlow.asStateFlow()

    private fun loadHistory(): List<VolumeFindHistory> {
        val jsonString = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<VolumeFindHistory>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.optString("id", UUID.randomUUID().toString())
                val shapeName = obj.getString("shape")
                val shape = try {
                    VolumeShape.valueOf(shapeName)
                } catch (_: Exception) {
                    VolumeShape.CUBE
                }

                val inputsObj = obj.getJSONObject("inputs")
                val inputs = mutableMapOf<String, String>()
                val inputKeys = inputsObj.keys()
                while (inputKeys.hasNext()) {
                    val k = inputKeys.next()
                    inputs[k] = inputsObj.getString(k)
                }

                val unitsObj = obj.getJSONObject("inputUnits")
                val inputUnits = mutableMapOf<String, LengthUnit>()
                val unitKeys = unitsObj.keys()
                while (unitKeys.hasNext()) {
                    val k = unitKeys.next()
                    val uName = unitsObj.getString(k)
                    inputUnits[k] = try {
                        LengthUnit.valueOf(uName)
                    } catch (_: Exception) {
                        LengthUnit.CENTIMETER
                    }
                }

                val resultValue = obj.getString("resultValue")
                val resultUnitName = obj.getString("resultUnit")
                val resultUnit = try {
                    VolumeUnit.valueOf(resultUnitName)
                } catch (_: Exception) {
                    VolumeUnit.CUBIC_CENTIMETER
                }
                val calculatedAt = obj.optLong("calculatedAt", System.currentTimeMillis())

                list.add(
                    VolumeFindHistory(
                        id = id,
                        shape = shape,
                        inputs = inputs,
                        inputUnits = inputUnits,
                        resultValue = resultValue,
                        resultUnit = resultUnit,
                        calculatedAt = calculatedAt
                    )
                )
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }

    private fun persistHistory(list: List<VolumeFindHistory>) {
        val jsonArray = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("shape", item.shape.name)

            val inputsObj = JSONObject()
            item.inputs.forEach { (k, v) -> inputsObj.put(k, v) }
            obj.put("inputs", inputsObj)

            val unitsObj = JSONObject()
            item.inputUnits.forEach { (k, v) -> unitsObj.put(k, v.name) }
            obj.put("inputUnits", unitsObj)

            obj.put("resultValue", item.resultValue)
            obj.put("resultUnit", item.resultUnit.name)
            obj.put("calculatedAt", item.calculatedAt)

            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
        _historyFlow.value = list
    }

    fun saveCalculation(
        shape: VolumeShape,
        inputs: Map<String, String>,
        inputUnits: Map<String, LengthUnit>,
        resultValue: String,
        resultUnit: VolumeUnit
    ): Boolean {
        val current = _historyFlow.value.toMutableList()

        // Consecutive duplicate check
        if (current.isNotEmpty()) {
            val latest = current.first()
            val isDuplicate = latest.shape == shape &&
                latest.inputs == inputs &&
                latest.inputUnits == inputUnits &&
                latest.resultUnit == resultUnit &&
                latest.resultValue == resultValue
            if (isDuplicate) {
                return false
            }
        }

        val newItem = VolumeFindHistory(
            id = UUID.randomUUID().toString(),
            shape = shape,
            inputs = inputs,
            inputUnits = inputUnits,
            resultValue = resultValue,
            resultUnit = resultUnit,
            calculatedAt = System.currentTimeMillis()
        )

        // Prepend to top
        current.add(0, newItem)

        // Limit to MAX_HISTORY entries (10)
        while (current.size > MAX_HISTORY) {
            current.removeAt(current.size - 1)
        }

        persistHistory(current)
        return true
    }

    fun deleteItem(id: String) {
        val current = _historyFlow.value.filter { it.id != id }
        persistHistory(current)
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
        _historyFlow.value = emptyList()
    }

    companion object {
        const val MAX_HISTORY = 10
        private const val KEY_HISTORY = "recent_history"
    }
}
