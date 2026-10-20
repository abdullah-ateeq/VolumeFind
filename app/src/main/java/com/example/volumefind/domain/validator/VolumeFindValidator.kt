package com.example.volumefind.domain.validator

import com.example.volumefind.domain.model.VolumeFindError
import com.example.volumefind.domain.model.VolumeShape
import com.example.volumefind.domain.parser.VolumeFindParser
import java.math.BigDecimal

object VolumeFindValidator {

    fun validateDimensionInput(
        input: String,
        dimensionName: String = "Value"
    ): Pair<BigDecimal?, String?> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Pair(null, "$dimensionName cannot be empty.")
        }
        if (trimmed.length > VolumeFindParser.MAX_INPUT_LENGTH) {
            return Pair(null, "Number is too long. Maximum 500 digits.")
        }

        val parseResult = VolumeFindParser.parseDimension(trimmed)
        if (parseResult.isFailure) {
            val message = parseResult.exceptionOrNull()?.message ?: "Enter a valid dimension."
            return Pair(null, message)
        }

        val value = parseResult.getOrNull()
        if (value == null || value <= BigDecimal.ZERO) {
            return Pair(null, "$dimensionName must be greater than zero.")
        }

        return Pair(value, null)
    }

    fun validateShapeInputs(
        shape: VolumeShape,
        inputs: Map<String, String>
    ): Pair<Map<String, BigDecimal>?, String?> {
        val parsedMap = mutableMapOf<String, BigDecimal>()
        val requiredKeys = getRequiredDimensionKeys(shape)

        for (key in requiredKeys) {
            val raw = inputs[key]
            if (raw.isNullOrBlank()) {
                val label = getDimensionLabel(shape, key)
                return Pair(null, "$label cannot be empty.")
            }
            val (value, error) = validateDimensionInput(raw, getDimensionLabel(shape, key))
            if (error != null) {
                return Pair(null, error)
            }
            if (value != null) {
                parsedMap[key] = value
            }
        }

        return Pair(parsedMap, null)
    }

    fun getRequiredDimensionKeys(shape: VolumeShape): List<String> = when (shape) {
        VolumeShape.CUBE -> listOf("side")
        VolumeShape.RECTANGULAR_PRISM -> listOf("length", "width", "height")
        VolumeShape.CYLINDER -> listOf("radius", "height")
        VolumeShape.SPHERE -> listOf("radius")
        VolumeShape.CONE -> listOf("radius", "height")
        VolumeShape.SQUARE_PYRAMID -> listOf("baseSide", "height")
        VolumeShape.TRIANGULAR_PRISM -> listOf("triangleBase", "triangleHeight", "prismLength")
        VolumeShape.HEMISPHERE -> listOf("radius")
    }

    fun getDimensionLabel(shape: VolumeShape, key: String): String = when (key) {
        "side" -> "Side"
        "length" -> "Length"
        "width" -> "Width"
        "height" -> "Height"
        "radius" -> "Radius"
        "baseSide" -> "Base Side"
        "triangleBase" -> "Triangle Base"
        "triangleHeight" -> "Triangle Height"
        "prismLength" -> "Prism Length"
        else -> key.replaceFirstChar { it.uppercase() }
    }
}
