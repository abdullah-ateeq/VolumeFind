package com.example.volumefind.domain.model

enum class LengthUnit(val symbol: String, val volumeSymbol: String) {
    MILLIMETER("mm", "mm³"),
    CENTIMETER("cm", "cm³"),
    METER("m", "m³"),
    KILOMETER("km", "km³"),
    INCH("in", "in³"),
    FOOT("ft", "ft³"),
    YARD("yd", "yd³");

    fun toVolumeUnit(): VolumeUnit = when (this) {
        MILLIMETER -> VolumeUnit.CUBIC_MILLIMETER
        CENTIMETER -> VolumeUnit.CUBIC_CENTIMETER
        METER -> VolumeUnit.CUBIC_METER
        KILOMETER -> VolumeUnit.CUBIC_KILOMETER
        INCH -> VolumeUnit.CUBIC_INCH
        FOOT -> VolumeUnit.CUBIC_FOOT
        YARD -> VolumeUnit.CUBIC_YARD
    }
}
