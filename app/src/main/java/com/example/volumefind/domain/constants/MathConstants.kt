package com.example.volumefind.domain.constants

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object MathConstants {
    val PI: BigDecimal = BigDecimal(
        "3.1415926535897932384626433832795028841971693993751"
    )

    val DEFAULT_MATH_CONTEXT: MathContext = MathContext(50, RoundingMode.HALF_UP)
}
