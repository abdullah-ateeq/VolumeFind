package com.example.volumefind.domain.calculator

import com.example.volumefind.domain.constants.MathConstants
import com.example.volumefind.domain.model.LengthUnit
import com.example.volumefind.domain.model.VolumeCalculationDetails
import com.example.volumefind.domain.model.VolumeShape
import com.example.volumefind.domain.model.VolumeUnit
import java.math.BigDecimal
import java.math.MathContext

object VolumeCalculator {

    fun calculateCube(
        side: BigDecimal,
        mathContext: MathContext
    ): BigDecimal {
        return side.multiply(side, mathContext).multiply(side, mathContext)
    }

    fun calculateRectangularPrism(
        length: BigDecimal,
        width: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext
    ): BigDecimal {
        return length.multiply(width, mathContext).multiply(height, mathContext)
    }

    fun calculateCylinder(
        radius: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext
    ): BigDecimal {
        val rSquared = radius.multiply(radius, mathContext)
        return MathConstants.PI.multiply(rSquared, mathContext).multiply(height, mathContext)
    }

    fun calculateSphere(
        radius: BigDecimal,
        mathContext: MathContext
    ): BigDecimal {
        val fourThirds = BigDecimal("4").divide(BigDecimal("3"), mathContext)
        val rCubed = radius.multiply(radius, mathContext).multiply(radius, mathContext)
        return fourThirds.multiply(MathConstants.PI, mathContext).multiply(rCubed, mathContext)
    }

    fun calculateCone(
        radius: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext
    ): BigDecimal {
        val oneThird = BigDecimal.ONE.divide(BigDecimal("3"), mathContext)
        val rSquared = radius.multiply(radius, mathContext)
        return oneThird.multiply(MathConstants.PI, mathContext).multiply(rSquared, mathContext).multiply(height, mathContext)
    }

    fun calculateSquarePyramid(
        baseSide: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext
    ): BigDecimal {
        val oneThird = BigDecimal.ONE.divide(BigDecimal("3"), mathContext)
        val sSquared = baseSide.multiply(baseSide, mathContext)
        return oneThird.multiply(sSquared, mathContext).multiply(height, mathContext)
    }

    fun calculateTriangularPrism(
        triangleBase: BigDecimal,
        triangleHeight: BigDecimal,
        prismLength: BigDecimal,
        mathContext: MathContext
    ): BigDecimal {
        val oneHalf = BigDecimal("0.5")
        return oneHalf.multiply(triangleBase, mathContext)
            .multiply(triangleHeight, mathContext)
            .multiply(prismLength, mathContext)
    }

    fun calculateHemisphere(
        radius: BigDecimal,
        mathContext: MathContext
    ): BigDecimal {
        val twoThirds = BigDecimal("2").divide(BigDecimal("3"), mathContext)
        val rCubed = radius.multiply(radius, mathContext).multiply(radius, mathContext)
        return twoThirds.multiply(MathConstants.PI, mathContext).multiply(rCubed, mathContext)
    }

    fun calculateShapeVolume(
        shape: VolumeShape,
        inputs: Map<String, BigDecimal>,
        inputUnits: Map<String, LengthUnit>,
        targetUnit: VolumeUnit,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): Pair<BigDecimal, VolumeCalculationDetails> {
        // Step 1: Normalize dimensions to meters
        val inMeters = inputs.mapValues { (key, value) ->
            val unit = inputUnits[key] ?: LengthUnit.CENTIMETER
            LengthUnitConverter.convertToMeters(value, unit, mathContext)
        }

        // Step 2: Calculate volume in cubic meters
        val volumeInMeters3 = when (shape) {
            VolumeShape.CUBE -> {
                val s = inMeters["side"] ?: BigDecimal.ZERO
                calculateCube(s, mathContext)
            }
            VolumeShape.RECTANGULAR_PRISM -> {
                val l = inMeters["length"] ?: BigDecimal.ZERO
                val w = inMeters["width"] ?: BigDecimal.ZERO
                val h = inMeters["height"] ?: BigDecimal.ZERO
                calculateRectangularPrism(l, w, h, mathContext)
            }
            VolumeShape.CYLINDER -> {
                val r = inMeters["radius"] ?: BigDecimal.ZERO
                val h = inMeters["height"] ?: BigDecimal.ZERO
                calculateCylinder(r, h, mathContext)
            }
            VolumeShape.SPHERE -> {
                val r = inMeters["radius"] ?: BigDecimal.ZERO
                calculateSphere(r, mathContext)
            }
            VolumeShape.CONE -> {
                val r = inMeters["radius"] ?: BigDecimal.ZERO
                val h = inMeters["height"] ?: BigDecimal.ZERO
                calculateCone(r, h, mathContext)
            }
            VolumeShape.SQUARE_PYRAMID -> {
                val s = inMeters["baseSide"] ?: BigDecimal.ZERO
                val h = inMeters["height"] ?: BigDecimal.ZERO
                calculateSquarePyramid(s, h, mathContext)
            }
            VolumeShape.TRIANGULAR_PRISM -> {
                val b = inMeters["triangleBase"] ?: BigDecimal.ZERO
                val h = inMeters["triangleHeight"] ?: BigDecimal.ZERO
                val l = inMeters["prismLength"] ?: BigDecimal.ZERO
                calculateTriangularPrism(b, h, l, mathContext)
            }
            VolumeShape.HEMISPHERE -> {
                val r = inMeters["radius"] ?: BigDecimal.ZERO
                calculateHemisphere(r, mathContext)
            }
        }

        // Step 3: Convert volume from m³ to target cubic unit
        val finalVolume = VolumeUnitConverter.convertVolume(
            value = volumeInMeters3,
            fromUnit = VolumeUnit.CUBIC_METER,
            toUnit = targetUnit,
            mathContext = mathContext
        )

        // Generate calculation explanation details
        val details = buildCalculationDetails(
            shape = shape,
            inputs = inputs,
            inputUnits = inputUnits,
            targetUnit = targetUnit,
            finalVolume = finalVolume
        )

        return Pair(finalVolume, details)
    }

    private fun buildCalculationDetails(
        shape: VolumeShape,
        inputs: Map<String, BigDecimal>,
        inputUnits: Map<String, LengthUnit>,
        targetUnit: VolumeUnit,
        finalVolume: BigDecimal
    ): VolumeCalculationDetails {
        val formula = shape.formulaString
        val unitSymbol = targetUnit.symbol

        // Check if all input units match target unit's length unit
        val allUnitsMatchTarget = inputs.keys.all { key ->
            inputUnits[key]?.toVolumeUnit() == targetUnit
        }

        val calculation = StringBuilder()
        calculation.append(formula).append("\n\n")

        if (allUnitsMatchTarget) {
            when (shape) {
                VolumeShape.CUBE -> {
                    val s = inputs["side"] ?: BigDecimal.ZERO
                    calculation.append("V = ${s.toPlainString()}³\n\n")
                    calculation.append("V = ${finalVolume.stripTrailingZeros().toPlainString()} $unitSymbol")
                }
                VolumeShape.RECTANGULAR_PRISM -> {
                    val l = inputs["length"] ?: BigDecimal.ZERO
                    val w = inputs["width"] ?: BigDecimal.ZERO
                    val h = inputs["height"] ?: BigDecimal.ZERO
                    calculation.append("V = ${l.toPlainString()} × ${w.toPlainString()} × ${h.toPlainString()}\n\n")
                    calculation.append("V = ${finalVolume.stripTrailingZeros().toPlainString()} $unitSymbol")
                }
                VolumeShape.CYLINDER -> {
                    val r = inputs["radius"] ?: BigDecimal.ZERO
                    val h = inputs["height"] ?: BigDecimal.ZERO
                    calculation.append("V = π × ${r.toPlainString()}² × ${h.toPlainString()}\n\n")
                    val preview = if (finalVolume.toPlainString().length > 10) {
                        finalVolume.toPlainString().take(10) + "..."
                    } else {
                        finalVolume.toPlainString()
                    }
                    calculation.append("V ≈ $preview $unitSymbol")
                }
                VolumeShape.SPHERE -> {
                    val r = inputs["radius"] ?: BigDecimal.ZERO
                    calculation.append("V = 4/3 × π × ${r.toPlainString()}³\n\n")
                    val preview = if (finalVolume.toPlainString().length > 10) {
                        finalVolume.toPlainString().take(10) + "..."
                    } else {
                        finalVolume.toPlainString()
                    }
                    calculation.append("V ≈ $preview $unitSymbol")
                }
                VolumeShape.CONE -> {
                    val r = inputs["radius"] ?: BigDecimal.ZERO
                    val h = inputs["height"] ?: BigDecimal.ZERO
                    calculation.append("V = 1/3 × π × ${r.toPlainString()}² × ${h.toPlainString()}\n\n")
                    val preview = if (finalVolume.toPlainString().length > 10) {
                        finalVolume.toPlainString().take(10) + "..."
                    } else {
                        finalVolume.toPlainString()
                    }
                    calculation.append("V ≈ $preview $unitSymbol")
                }
                VolumeShape.SQUARE_PYRAMID -> {
                    val s = inputs["baseSide"] ?: BigDecimal.ZERO
                    val h = inputs["height"] ?: BigDecimal.ZERO
                    val s2 = s.multiply(s).toPlainString()
                    calculation.append("V = 1/3 × $s2 × ${h.toPlainString()}\n\n")
                    calculation.append("V = ${finalVolume.stripTrailingZeros().toPlainString()} $unitSymbol")
                }
                VolumeShape.TRIANGULAR_PRISM -> {
                    val b = inputs["triangleBase"] ?: BigDecimal.ZERO
                    val h = inputs["triangleHeight"] ?: BigDecimal.ZERO
                    val l = inputs["prismLength"] ?: BigDecimal.ZERO
                    calculation.append("V = 1/2 × ${b.toPlainString()} × ${h.toPlainString()} × ${l.toPlainString()}\n\n")
                    calculation.append("V = ${finalVolume.stripTrailingZeros().toPlainString()} $unitSymbol")
                }
                VolumeShape.HEMISPHERE -> {
                    val r = inputs["radius"] ?: BigDecimal.ZERO
                    calculation.append("V = 2/3 × π × ${r.toPlainString()}³\n\n")
                    val preview = if (finalVolume.toPlainString().length > 10) {
                        finalVolume.toPlainString().take(10) + "..."
                    } else {
                        finalVolume.toPlainString()
                    }
                    calculation.append("V ≈ $preview $unitSymbol")
                }
            }
        } else {
            // Mixed units or different output unit
            calculation.append("Dimensions normalized to meters:\n")
            inputs.forEach { (key, value) ->
                val unit = inputUnits[key] ?: LengthUnit.CENTIMETER
                val inM = LengthUnitConverter.convertToMeters(value, unit, MathConstants.DEFAULT_MATH_CONTEXT)
                calculation.append("• $key = ${value.toPlainString()} ${unit.symbol} = ${inM.stripTrailingZeros().toPlainString()} m\n")
            }
            calculation.append("\nConverted to output unit:\n")
            calculation.append("V = ${finalVolume.stripTrailingZeros().toPlainString()} $unitSymbol")
        }

        return VolumeCalculationDetails(
            formula = formula,
            calculation = calculation.toString()
        )
    }
}
