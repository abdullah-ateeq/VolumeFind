package com.example.volumefind.domain.model

import java.math.BigDecimal

data class LengthUnitDefinition(
    val unit: LengthUnit,
    val name: String,
    val symbol: String,
    val metersFactor: BigDecimal
)
