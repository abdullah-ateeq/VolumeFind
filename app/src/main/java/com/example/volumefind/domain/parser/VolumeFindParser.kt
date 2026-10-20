package com.example.volumefind.domain.parser

import java.math.BigDecimal

object VolumeFindParser {
    const val MAX_INPUT_LENGTH = 500

    fun parseDimension(input: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Dimension cannot be empty."))
        }

        if (trimmed.length > MAX_INPUT_LENGTH) {
            return Result.failure(IllegalArgumentException("Number is too long. Maximum 500 digits."))
        }

        // Normalize leading or trailing decimal point
        var normalized = trimmed

        // Replace leading decimal e.g. .5 -> 0.5 or +.5 -> +0.5 or -.5 -> -0.5
        if (normalized.startsWith(".")) {
            normalized = "0$normalized"
        } else if (normalized.startsWith("+.")) {
            normalized = "+0" + normalized.substring(2)
        } else if (normalized.startsWith("-.")) {
            normalized = "-0" + normalized.substring(2)
        }

        // Handle trailing decimal e.g. 10. -> 10.0 or 10.E2 -> 10.0E2
        if (normalized.endsWith(".")) {
            normalized = normalized.dropLast(1)
        } else if (normalized.contains(".e", ignoreCase = true)) {
            val parts = normalized.split(Regex("[eE]"), limit = 2)
            val mantissa = if (parts[0].endsWith(".")) parts[0].dropLast(1) else parts[0]
            val exponent = if (parts.size > 1) parts[1] else ""
            normalized = "${mantissa}E$exponent"
        }

        // Basic sanity check to avoid binary/hex or unwanted characters
        if (!normalized.matches(Regex("^[+-]?[0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?$"))) {
            return Result.failure(IllegalArgumentException("Enter a valid dimension."))
        }

        return try {
            val bigDecimal = BigDecimal(normalized)
            Result.success(bigDecimal)
        } catch (_: NumberFormatException) {
            Result.failure(IllegalArgumentException("Enter a valid dimension."))
        }
    }
}
