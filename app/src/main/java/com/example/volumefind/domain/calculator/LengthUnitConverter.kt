package com.example.volumefind.domain.calculator

import com.example.volumefind.domain.model.LengthUnit
import com.example.volumefind.domain.registry.LengthUnitRegistry
import java.math.BigDecimal
import java.math.MathContext

object LengthUnitConverter {
    fun convertToMeters(
        value: BigDecimal,
        unit: LengthUnit,
        mathContext: MathContext
    ): BigDecimal {
        val def = LengthUnitRegistry.get(unit)
        return value.multiply(def.metersFactor, mathContext)
    }

    fun convertFromMeters(
        value: BigDecimal,
        unit: LengthUnit,
        mathContext: MathContext
    ): BigDecimal {
        val def = LengthUnitRegistry.get(unit)
        return value.divide(def.metersFactor, mathContext)
    }
}
