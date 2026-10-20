package com.example.volumefind.domain.calculator

import com.example.volumefind.domain.model.VolumeUnit
import com.example.volumefind.domain.registry.VolumeUnitRegistry
import java.math.BigDecimal
import java.math.MathContext

object VolumeUnitConverter {
    fun convertVolume(
        value: BigDecimal,
        fromUnit: VolumeUnit,
        toUnit: VolumeUnit,
        mathContext: MathContext
    ): BigDecimal {
        if (fromUnit == toUnit) return value
        val fromDef = VolumeUnitRegistry.get(fromUnit)
        val toDef = VolumeUnitRegistry.get(toUnit)
        val valueInMetersCubed = value.multiply(fromDef.cubicConversionFactor, mathContext)
        return valueInMetersCubed.divide(toDef.cubicConversionFactor, mathContext)
    }
}
