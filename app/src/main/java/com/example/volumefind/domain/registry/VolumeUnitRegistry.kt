package com.example.volumefind.domain.registry

import com.example.volumefind.domain.constants.MathConstants
import com.example.volumefind.domain.model.VolumeUnit
import com.example.volumefind.domain.model.VolumeUnitDefinition
import java.math.BigDecimal

object VolumeUnitRegistry {
    private val mc = MathConstants.DEFAULT_MATH_CONTEXT

    val CUBIC_MILLIMETER = VolumeUnitDefinition(
        unit = VolumeUnit.CUBIC_MILLIMETER,
        name = "Cubic Millimeter",
        symbol = "mm³",
        lengthFactor = BigDecimal("0.001"),
        cubicConversionFactor = BigDecimal("0.001").pow(3, mc)
    )

    val CUBIC_CENTIMETER = VolumeUnitDefinition(
        unit = VolumeUnit.CUBIC_CENTIMETER,
        name = "Cubic Centimeter",
        symbol = "cm³",
        lengthFactor = BigDecimal("0.01"),
        cubicConversionFactor = BigDecimal("0.01").pow(3, mc)
    )

    val CUBIC_METER = VolumeUnitDefinition(
        unit = VolumeUnit.CUBIC_METER,
        name = "Cubic Meter",
        symbol = "m³",
        lengthFactor = BigDecimal("1"),
        cubicConversionFactor = BigDecimal.ONE
    )

    val CUBIC_KILOMETER = VolumeUnitDefinition(
        unit = VolumeUnit.CUBIC_KILOMETER,
        name = "Cubic Kilometer",
        symbol = "km³",
        lengthFactor = BigDecimal("1000"),
        cubicConversionFactor = BigDecimal("1000").pow(3, mc)
    )

    val CUBIC_INCH = VolumeUnitDefinition(
        unit = VolumeUnit.CUBIC_INCH,
        name = "Cubic Inch",
        symbol = "in³",
        lengthFactor = BigDecimal("0.0254"),
        cubicConversionFactor = BigDecimal("0.0254").pow(3, mc)
    )

    val CUBIC_FOOT = VolumeUnitDefinition(
        unit = VolumeUnit.CUBIC_FOOT,
        name = "Cubic Foot",
        symbol = "ft³",
        lengthFactor = BigDecimal("0.3048"),
        cubicConversionFactor = BigDecimal("0.3048").pow(3, mc)
    )

    val CUBIC_YARD = VolumeUnitDefinition(
        unit = VolumeUnit.CUBIC_YARD,
        name = "Cubic Yard",
        symbol = "yd³",
        lengthFactor = BigDecimal("0.9144"),
        cubicConversionFactor = BigDecimal("0.9144").pow(3, mc)
    )

    val allUnits: List<VolumeUnitDefinition> = listOf(
        CUBIC_MILLIMETER,
        CUBIC_CENTIMETER,
        CUBIC_METER,
        CUBIC_KILOMETER,
        CUBIC_INCH,
        CUBIC_FOOT,
        CUBIC_YARD
    )

    private val map: Map<VolumeUnit, VolumeUnitDefinition> = allUnits.associateBy { it.unit }

    fun get(unit: VolumeUnit): VolumeUnitDefinition =
        map[unit] ?: CUBIC_CENTIMETER

    fun search(query: String): List<VolumeUnitDefinition> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return allUnits
        return allUnits.filter { def ->
            def.name.lowercase().contains(q) ||
                def.symbol.lowercase().contains(q) ||
                (def.unit == VolumeUnit.CUBIC_FOOT && (q.contains("feet") || q.contains("foot") || q.contains("ft"))) ||
                (def.unit == VolumeUnit.CUBIC_INCH && (q.contains("inch") || q.contains("in"))) ||
                (def.unit == VolumeUnit.CUBIC_YARD && (q.contains("yard") || q.contains("yd"))) ||
                (def.unit == VolumeUnit.CUBIC_METER && (q.contains("meter") || q.contains("m"))) ||
                (def.unit == VolumeUnit.CUBIC_CENTIMETER && (q.contains("centimeter") || q.contains("cm"))) ||
                (def.unit == VolumeUnit.CUBIC_MILLIMETER && (q.contains("millimeter") || q.contains("mm"))) ||
                (def.unit == VolumeUnit.CUBIC_KILOMETER && (q.contains("kilometer") || q.contains("km")))
        }
    }
}
