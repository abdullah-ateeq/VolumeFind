package com.example.volumefind.domain.formatter

import com.example.volumefind.domain.model.LengthUnit
import com.example.volumefind.domain.model.VolumeCalculationDetails
import com.example.volumefind.domain.model.VolumeResult
import com.example.volumefind.domain.model.VolumeUnit
import java.math.BigDecimal
import java.math.RoundingMode

object VolumeFindFormatter {

    fun formatVolume(
        value: BigDecimal,
        unit: LengthUnit,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        return formatVolume(value, unit.toVolumeUnit(), decimalPlaces, roundingMode)
    }

    fun formatVolume(
        value: BigDecimal,
        unit: VolumeUnit,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val numberString = formatNumber(value, decimalPlaces, roundingMode)
        return "$numberString ${unit.symbol}"
    }

    fun formatNumber(
        value: BigDecimal,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        return if (decimalPlaces <= 0) {
            value.setScale(0, roundingMode).toPlainString()
        } else {
            val scaled = value.setScale(decimalPlaces, roundingMode)
            val stripped = scaled.stripTrailingZeros()
            // Ensure no scientific notation for typical values
            stripped.toPlainString()
        }
    }

    fun formatFormula(
        details: VolumeCalculationDetails
    ): String {
        return details.formula
    }

    fun formatCopyText(
        result: VolumeResult
    ): String {
        val sb = StringBuilder()
        sb.append("VolumeFind\n\n")
        sb.append("Shape: ${result.shape.displayName}\n")
        val formattedVal = formatVolume(result.value, result.outputUnit, 6, RoundingMode.HALF_UP)
        sb.append("Volume: $formattedVal")
        return sb.toString()
    }

    fun formatCopyTextWithInputs(
        result: VolumeResult,
        inputs: Map<String, String>,
        inputUnits: Map<String, LengthUnit>,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val sb = StringBuilder()
        sb.append("VolumeFind\n\n")
        sb.append("Shape: ${result.shape.displayName}\n")
        inputs.forEach { (key, value) ->
            val unit = inputUnits[key] ?: LengthUnit.CENTIMETER
            val label = key.replaceFirstChar { it.uppercase() }
            sb.append("$label: $value ${unit.symbol}\n")
        }
        val formattedVal = formatVolume(result.value, result.outputUnit, decimalPlaces, roundingMode)
        sb.append("Volume: $formattedVal")
        return sb.toString()
    }

    fun formatShareText(
        result: VolumeResult
    ): String {
        return formatCopyText(result)
    }

    fun formatShareTextWithInputs(
        result: VolumeResult,
        inputs: Map<String, String>,
        inputUnits: Map<String, LengthUnit>,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        return formatCopyTextWithInputs(result, inputs, inputUnits, decimalPlaces, roundingMode)
    }
}
