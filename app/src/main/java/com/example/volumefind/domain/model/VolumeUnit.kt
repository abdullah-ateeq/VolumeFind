package com.example.volumefind.domain.model

enum class VolumeUnit(val symbol: String) {
    CUBIC_MILLIMETER("mm³"),
    CUBIC_CENTIMETER("cm³"),
    CUBIC_METER("m³"),
    CUBIC_KILOMETER("km³"),
    CUBIC_INCH("in³"),
    CUBIC_FOOT("ft³"),
    CUBIC_YARD("yd³");

    fun toLengthUnit(): LengthUnit = when (this) {
        CUBIC_MILLIMETER -> LengthUnit.MILLIMETER
        CUBIC_CENTIMETER -> LengthUnit.CENTIMETER
        CUBIC_METER -> LengthUnit.METER
        CUBIC_KILOMETER -> LengthUnit.KILOMETER
        CUBIC_INCH -> LengthUnit.INCH
        CUBIC_FOOT -> LengthUnit.FOOT
        CUBIC_YARD -> LengthUnit.YARD
    }
}
