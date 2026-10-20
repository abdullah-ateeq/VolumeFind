package com.example.volumefind.domain.model

import java.math.BigDecimal

data class VolumeUnitDefinition(
    val unit: VolumeUnit,
    val name: String,
    val symbol: String,
    val lengthFactor: BigDecimal,
    val cubicConversionFactor: BigDecimal
)
