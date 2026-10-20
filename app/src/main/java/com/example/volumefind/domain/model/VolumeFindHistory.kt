package com.example.volumefind.domain.model

data class VolumeFindHistory(
    val id: String,
    val shape: VolumeShape,
    val inputs: Map<String, String>,
    val inputUnits: Map<String, LengthUnit>,
    val resultValue: String,
    val resultUnit: VolumeUnit,
    val calculatedAt: Long
)
