package com.example.volumefind.ui.home

import com.example.volumefind.domain.model.LengthUnit
import com.example.volumefind.domain.model.VolumeFindHistory
import com.example.volumefind.domain.model.VolumeResult
import com.example.volumefind.domain.model.VolumeShape
import com.example.volumefind.domain.model.VolumeUnit
import java.math.RoundingMode

data class VolumeFindUiState(
    val shape: VolumeShape = VolumeShape.CUBE,
    val inputs: Map<String, String> = mapOf("side" to "5"),
    val inputUnits: Map<String, LengthUnit> = mapOf("side" to LengthUnit.CENTIMETER),
    val resultUnit: VolumeUnit = VolumeUnit.CUBIC_CENTIMETER,
    val result: VolumeResult? = null,
    val history: List<VolumeFindHistory> = emptyList(),
    val decimalPlaces: Int = 6,
    val roundingMode: RoundingMode = RoundingMode.HALF_UP,
    val showFormula: Boolean = true,
    val showCalculationSteps: Boolean = true,
    val showShapeDiagram: Boolean = true,
    val errorMessage: String? = null,
    val isCalculating: Boolean = false
)
