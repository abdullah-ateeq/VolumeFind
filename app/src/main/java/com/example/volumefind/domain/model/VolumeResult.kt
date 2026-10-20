package com.example.volumefind.domain.model

import java.math.BigDecimal

data class VolumeResult(
    val shape: VolumeShape,
    val value: BigDecimal,
    val outputUnit: LengthUnit,
    val details: VolumeCalculationDetails,
    val calculatedAt: Long
) {
    val volumeUnit: VolumeUnit
        get() = outputUnit.toVolumeUnit()
}
