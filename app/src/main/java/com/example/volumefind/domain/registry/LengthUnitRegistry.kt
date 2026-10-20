package com.example.volumefind.domain.registry

import com.example.volumefind.domain.model.LengthUnit
import com.example.volumefind.domain.model.LengthUnitDefinition
import java.math.BigDecimal

object LengthUnitRegistry {
    val MILLIMETER = LengthUnitDefinition(
        unit = LengthUnit.MILLIMETER,
        name = "Millimeter",
        symbol = "mm",
        metersFactor = BigDecimal("0.001")
    )
    val CENTIMETER = LengthUnitDefinition(
        unit = LengthUnit.CENTIMETER,
        name = "Centimeter",
        symbol = "cm",
        metersFactor = BigDecimal("0.01")
    )
    val METER = LengthUnitDefinition(
        unit = LengthUnit.METER,
        name = "Meter",
        symbol = "m",
        metersFactor = BigDecimal("1")
    )
    val KILOMETER = LengthUnitDefinition(
        unit = LengthUnit.KILOMETER,
        name = "Kilometer",
        symbol = "km",
        metersFactor = BigDecimal("1000")
    )
    val INCH = LengthUnitDefinition(
        unit = LengthUnit.INCH,
        name = "Inch",
        symbol = "in",
        metersFactor = BigDecimal("0.0254")
    )
    val FOOT = LengthUnitDefinition(
        unit = LengthUnit.FOOT,
        name = "Foot",
        symbol = "ft",
        metersFactor = BigDecimal("0.3048")
    )
    val YARD = LengthUnitDefinition(
        unit = LengthUnit.YARD,
        name = "Yard",
        symbol = "yd",
        metersFactor = BigDecimal("0.9144")
    )

    val allUnits: List<LengthUnitDefinition> = listOf(
        MILLIMETER,
        CENTIMETER,
        METER,
        KILOMETER,
        INCH,
        FOOT,
        YARD
    )

    private val map: Map<LengthUnit, LengthUnitDefinition> = allUnits.associateBy { it.unit }

    fun get(unit: LengthUnit): LengthUnitDefinition =
        map[unit] ?: CENTIMETER

    fun search(query: String): List<LengthUnitDefinition> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return allUnits
        return allUnits.filter { def ->
            def.name.lowercase().contains(q) ||
                def.symbol.lowercase().contains(q) ||
                (def.unit == LengthUnit.FOOT && (q == "feet" || q == "foot" || q == "ft")) ||
                (def.unit == LengthUnit.INCH && (q == "inch" || q == "inches" || q == "in")) ||
                (def.unit == LengthUnit.YARD && (q == "yard" || q == "yards" || q == "yd")) ||
                (def.unit == LengthUnit.METER && (q == "meter" || q == "meters" || q == "m")) ||
                (def.unit == LengthUnit.CENTIMETER && (q == "centimeter" || q == "centimeters" || q == "cm")) ||
                (def.unit == LengthUnit.MILLIMETER && (q == "millimeter" || q == "millimeters" || q == "mm")) ||
                (def.unit == LengthUnit.KILOMETER && (q == "kilometer" || q == "kilometers" || q == "km"))
        }
    }
}
