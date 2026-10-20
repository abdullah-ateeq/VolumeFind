package com.example.volumefind

import com.example.volumefind.data.repository.AppSettings
import com.example.volumefind.data.repository.SettingsRepository
import com.example.volumefind.data.repository.VolumeFindHistoryRepository
import com.example.volumefind.domain.calculator.LengthUnitConverter
import com.example.volumefind.domain.calculator.VolumeCalculator
import com.example.volumefind.domain.calculator.VolumeUnitConverter
import com.example.volumefind.domain.constants.MathConstants
import com.example.volumefind.domain.formatter.VolumeFindFormatter
import com.example.volumefind.domain.model.LengthUnit
import com.example.volumefind.domain.model.VolumeCalculationDetails
import com.example.volumefind.domain.model.VolumeFindHistory
import com.example.volumefind.domain.model.VolumeResult
import com.example.volumefind.domain.model.VolumeShape
import com.example.volumefind.domain.model.VolumeUnit
import com.example.volumefind.domain.parser.VolumeFindParser
import com.example.volumefind.domain.registry.LengthUnitRegistry
import com.example.volumefind.domain.registry.VolumeUnitRegistry
import com.example.volumefind.domain.validator.VolumeFindValidator
import com.example.volumefind.testutil.FakeSharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class VolumeFindComprehensiveTests {

    private val mc = MathConstants.DEFAULT_MATH_CONTEXT

    // 1. Cube
    @Test
    fun test01_CubeCalculation() {
        val side = BigDecimal("5")
        val volume = VolumeCalculator.calculateCube(side, mc)
        assertEquals(0, BigDecimal("125").compareTo(volume))

        val (calcVol, details) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to BigDecimal("5")),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        val formatted = VolumeFindFormatter.formatVolume(calcVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("125 cm³", formatted)
        assertTrue(details.calculation.contains("125"))
    }

    // 2. Rectangular prism
    @Test
    fun test02_RectangularPrismCalculation() {
        val l = BigDecimal("10")
        val w = BigDecimal("5")
        val h = BigDecimal("4")
        val volume = VolumeCalculator.calculateRectangularPrism(l, w, h, mc)
        assertEquals(0, BigDecimal("200").compareTo(volume))

        val (calcVol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.RECTANGULAR_PRISM,
            inputs = mapOf("length" to l, "width" to w, "height" to h),
            inputUnits = mapOf("length" to LengthUnit.CENTIMETER, "width" to LengthUnit.CENTIMETER, "height" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        val formatted = VolumeFindFormatter.formatVolume(calcVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("200 cm³", formatted)
    }

    // 3. Cylinder
    @Test
    fun test03_CylinderCalculation() {
        val r = BigDecimal("3")
        val h = BigDecimal("10")
        val (calcVol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.CYLINDER,
            inputs = mapOf("radius" to r, "height" to h),
            inputUnits = mapOf("radius" to LengthUnit.CENTIMETER, "height" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        val formatted = VolumeFindFormatter.formatVolume(calcVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("282.743339 cm³", formatted)
    }

    // 4. Sphere
    @Test
    fun test04_SphereCalculation() {
        val r = BigDecimal("3")
        val (calcVol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.SPHERE,
            inputs = mapOf("radius" to r),
            inputUnits = mapOf("radius" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        val formatted = VolumeFindFormatter.formatVolume(calcVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("113.097336 cm³", formatted)
    }

    // 5. Cone
    @Test
    fun test05_ConeCalculation() {
        val r = BigDecimal("3")
        val h = BigDecimal("10")
        val (calcVol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.CONE,
            inputs = mapOf("radius" to r, "height" to h),
            inputUnits = mapOf("radius" to LengthUnit.CENTIMETER, "height" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        val formatted = VolumeFindFormatter.formatVolume(calcVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("94.24778 cm³", formatted)
    }

    // 6. Square pyramid
    @Test
    fun test06_SquarePyramidCalculation() {
        val s = BigDecimal("5")
        val h = BigDecimal("12")
        val (calcVol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.SQUARE_PYRAMID,
            inputs = mapOf("baseSide" to s, "height" to h),
            inputUnits = mapOf("baseSide" to LengthUnit.CENTIMETER, "height" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        val formatted = VolumeFindFormatter.formatVolume(calcVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("100 cm³", formatted)
    }

    // 7. Triangular prism
    @Test
    fun test07_TriangularPrismCalculation() {
        val b = BigDecimal("6")
        val h = BigDecimal("4")
        val l = BigDecimal("10")
        val (calcVol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.TRIANGULAR_PRISM,
            inputs = mapOf("triangleBase" to b, "triangleHeight" to h, "prismLength" to l),
            inputUnits = mapOf("triangleBase" to LengthUnit.CENTIMETER, "triangleHeight" to LengthUnit.CENTIMETER, "prismLength" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        val formatted = VolumeFindFormatter.formatVolume(calcVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("120 cm³", formatted)
    }

    // 8. Hemisphere
    @Test
    fun test08_HemisphereCalculation() {
        val r = BigDecimal("3")
        val (calcVol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.HEMISPHERE,
            inputs = mapOf("radius" to r),
            inputUnits = mapOf("radius" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        val formatted = VolumeFindFormatter.formatVolume(calcVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("56.548668 cm³", formatted)
    }

    // 9. mm conversion
    @Test
    fun test09_MillimeterConversion() {
        val inMm = BigDecimal("1000")
        val inM = LengthUnitConverter.convertToMeters(inMm, LengthUnit.MILLIMETER, mc)
        assertEquals(0, BigDecimal("1").compareTo(inM))
        val backToMm = LengthUnitConverter.convertFromMeters(inM, LengthUnit.MILLIMETER, mc)
        assertEquals(0, BigDecimal("1000").compareTo(backToMm))
    }

    // 10. cm conversion
    @Test
    fun test10_CentimeterConversion() {
        val inCm = BigDecimal("100")
        val inM = LengthUnitConverter.convertToMeters(inCm, LengthUnit.CENTIMETER, mc)
        assertEquals(0, BigDecimal("1").compareTo(inM))
    }

    // 11. m conversion
    @Test
    fun test11_MeterConversion() {
        val inM = BigDecimal("42")
        val converted = LengthUnitConverter.convertToMeters(inM, LengthUnit.METER, mc)
        assertEquals(0, BigDecimal("42").compareTo(converted))
    }

    // 12. km conversion
    @Test
    fun test12_KilometerConversion() {
        val inKm = BigDecimal("1.5")
        val inM = LengthUnitConverter.convertToMeters(inKm, LengthUnit.KILOMETER, mc)
        assertEquals(0, BigDecimal("1500").compareTo(inM))
    }

    // 13. inch conversion
    @Test
    fun test13_InchConversion() {
        val inInch = BigDecimal("1")
        val inM = LengthUnitConverter.convertToMeters(inInch, LengthUnit.INCH, mc)
        assertEquals(0, BigDecimal("0.0254").compareTo(inM))

        // 1 in cube in cm³ = 16.387064 cm³
        val (vol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to BigDecimal("1")),
            inputUnits = mapOf("side" to LengthUnit.INCH),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        val formatted = VolumeFindFormatter.formatVolume(vol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("16.387064 cm³", formatted)
    }

    // 14. foot conversion
    @Test
    fun test14_FootConversion() {
        val inFt = BigDecimal("1")
        val inM = LengthUnitConverter.convertToMeters(inFt, LengthUnit.FOOT, mc)
        assertEquals(0, BigDecimal("0.3048").compareTo(inM))

        // 1 ft cube in m³ = 0.028316846592 m³
        val (vol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to BigDecimal("1")),
            inputUnits = mapOf("side" to LengthUnit.FOOT),
            targetUnit = VolumeUnit.CUBIC_METER
        )
        val formatted = VolumeFindFormatter.formatVolume(vol, VolumeUnit.CUBIC_METER, 12, RoundingMode.HALF_UP)
        assertEquals("0.028316846592 m³", formatted)
    }

    // 15. yard conversion
    @Test
    fun test15_YardConversion() {
        val inYd = BigDecimal("1")
        val inM = LengthUnitConverter.convertToMeters(inYd, LengthUnit.YARD, mc)
        assertEquals(0, BigDecimal("0.9144").compareTo(inM))
    }

    // 16. Cubic unit conversion
    @Test
    fun test16_CubicUnitConversion() {
        val oneM3 = BigDecimal("1")
        val inCm3 = VolumeUnitConverter.convertVolume(oneM3, VolumeUnit.CUBIC_METER, VolumeUnit.CUBIC_CENTIMETER, mc)
        assertEquals(0, BigDecimal("1000000").compareTo(inCm3))

        val inFt3 = VolumeUnitConverter.convertVolume(oneM3, VolumeUnit.CUBIC_METER, VolumeUnit.CUBIC_FOOT, mc)
        val formattedFt3 = VolumeFindFormatter.formatNumber(inFt3, 10, RoundingMode.HALF_UP)
        assertTrue(formattedFt3.startsWith("35.31466672"))
    }

    // 17. Mixed input units
    @Test
    fun test17_MixedInputUnits() {
        // Rectangular prism: Length = 1 m, Width = 50 cm, Height = 1000 mm -> 0.5 m³
        val (vol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.RECTANGULAR_PRISM,
            inputs = mapOf("length" to BigDecimal("1"), "width" to BigDecimal("50"), "height" to BigDecimal("1000")),
            inputUnits = mapOf("length" to LengthUnit.METER, "width" to LengthUnit.CENTIMETER, "height" to LengthUnit.MILLIMETER),
            targetUnit = VolumeUnit.CUBIC_METER
        )
        val formatted = VolumeFindFormatter.formatVolume(vol, VolumeUnit.CUBIC_METER, 6, RoundingMode.HALF_UP)
        assertEquals("0.5 m³", formatted)
    }

    // 18. Result unit conversion
    @Test
    fun test18_ResultUnitConversion() {
        // Cube 5 cm
        val (volM3, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to BigDecimal("5")),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_METER
        )
        val formattedM3 = VolumeFindFormatter.formatVolume(volM3, VolumeUnit.CUBIC_METER, 6, RoundingMode.HALF_UP)
        assertEquals("0.000125 m³", formattedM3)

        val (volMm3, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to BigDecimal("5")),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_MILLIMETER
        )
        val formattedMm3 = VolumeFindFormatter.formatVolume(volMm3, VolumeUnit.CUBIC_MILLIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("125000 mm³", formattedMm3)
    }

    // 19. Zero input
    @Test
    fun test19_ZeroInput() {
        val (value, error) = VolumeFindValidator.validateDimensionInput("0", "Side")
        assertNull(value)
        assertEquals("Side must be greater than zero.", error)
    }

    // 20. Negative input
    @Test
    fun test20_NegativeInput() {
        val (value, error) = VolumeFindValidator.validateDimensionInput("-5", "Radius")
        assertNull(value)
        assertEquals("Radius must be greater than zero.", error)
    }

    // 21. Decimal input
    @Test
    fun test21_DecimalInput() {
        val (value, error) = VolumeFindValidator.validateDimensionInput("2.5")
        assertNull(error)
        assertNotNull(value)
        val cubeVol = VolumeCalculator.calculateCube(value!!, mc)
        val formatted = VolumeFindFormatter.formatVolume(cubeVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("15.625 cm³", formatted)
    }

    // 22. Leading decimal
    @Test
    fun test22_LeadingDecimal() {
        val parsed = VolumeFindParser.parseDimension(".5")
        assertTrue(parsed.isSuccess)
        assertEquals(0, BigDecimal("0.5").compareTo(parsed.getOrThrow()))
        val cubeVol = VolumeCalculator.calculateCube(parsed.getOrThrow(), mc)
        val formatted = VolumeFindFormatter.formatVolume(cubeVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("0.125 cm³", formatted)
    }

    // 23. Trailing decimal
    @Test
    fun test23_TrailingDecimal() {
        val parsed = VolumeFindParser.parseDimension("5.")
        assertTrue(parsed.isSuccess)
        assertEquals(0, BigDecimal("5").compareTo(parsed.getOrThrow()))
        val cubeVol = VolumeCalculator.calculateCube(parsed.getOrThrow(), mc)
        val formatted = VolumeFindFormatter.formatVolume(cubeVol, VolumeUnit.CUBIC_CENTIMETER, 6, RoundingMode.HALF_UP)
        assertEquals("125 cm³", formatted)
    }

    // 24. Scientific notation
    @Test
    fun test24_ScientificNotation() {
        val parsed = VolumeFindParser.parseDimension("1E+1")
        assertTrue(parsed.isSuccess)
        assertEquals(0, BigDecimal("10").compareTo(parsed.getOrThrow()))
        val cubeVol = VolumeCalculator.calculateCube(parsed.getOrThrow(), mc)
        assertEquals(0, BigDecimal("1000").compareTo(cubeVol))
    }

    // 25. Very small values
    @Test
    fun test25_VerySmallValues() {
        val small = "0.000001"
        val parsed = VolumeFindParser.parseDimension(small)
        assertTrue(parsed.isSuccess)
        val vol = VolumeCalculator.calculateCube(parsed.getOrThrow(), mc)
        // 1e-6 cubed = 1e-18
        assertEquals(0, BigDecimal("0.000000000000000001").compareTo(vol))
    }

    // 26. Very large values
    @Test
    fun test26_VeryLargeValues() {
        val huge = "999999999999999999999999"
        val parsed = VolumeFindParser.parseDimension(huge)
        assertTrue(parsed.isSuccess)
        val vol = VolumeCalculator.calculateCube(parsed.getOrThrow(), mc)
        assertTrue(vol > BigDecimal.ZERO)
    }

    // 27. BigDecimal precision
    @Test
    fun test27_BigDecimalPrecision() {
        val val01 = BigDecimal("0.1")
        val cube = VolumeCalculator.calculateCube(val01, mc)
        assertEquals(0, BigDecimal("0.001").compareTo(cube))
    }

    // 28. High-precision π
    @Test
    fun test28_HighPrecisionPi() {
        val piString = MathConstants.PI.toPlainString()
        assertTrue(piString.startsWith("3.1415926535897932384626433832795028841971693993751"))
        assertTrue(piString.length >= 50)
    }

    // 29. Radius squared
    @Test
    fun test29_RadiusSquared() {
        val r = BigDecimal("7")
        val r2 = r.multiply(r, mc)
        assertEquals(0, BigDecimal("49").compareTo(r2))
    }

    // 30. Radius cubed
    @Test
    fun test30_RadiusCubed() {
        val r = BigDecimal("4")
        val r3 = r.multiply(r, mc).multiply(r, mc)
        assertEquals(0, BigDecimal("64").compareTo(r3))
    }

    // 31. Side cubed
    @Test
    fun test31_SideCubed() {
        val s = BigDecimal("6")
        val s3 = VolumeCalculator.calculateCube(s, mc)
        assertEquals(0, BigDecimal("216").compareTo(s3))
    }

    // 32. Fraction calculations
    @Test
    fun test32_FractionCalculations() {
        val oneThird = BigDecimal.ONE.divide(BigDecimal("3"), mc)
        val twoThirds = BigDecimal("2").divide(BigDecimal("3"), mc)
        val fourThirds = BigDecimal("4").divide(BigDecimal("3"), mc)
        val oneHalf = BigDecimal.ONE.divide(BigDecimal("2"), mc)

        assertTrue(oneThird.toPlainString().startsWith("0.33333333333333333333"))
        assertTrue(twoThirds.toPlainString().startsWith("0.66666666666666666666"))
        assertTrue(fourThirds.toPlainString().startsWith("1.33333333333333333333"))
        assertEquals(0, BigDecimal("0.5").compareTo(oneHalf))
    }

    // 33. Same-unit result
    @Test
    fun test33_SameUnitResult() {
        val (vol, _) = VolumeCalculator.calculateShapeVolume(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to BigDecimal("10")),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        assertEquals(0, BigDecimal("1000").compareTo(vol))
    }

    // 34. Invalid input
    @Test
    fun test34_InvalidInput() {
        val r1 = VolumeFindParser.parseDimension("abc")
        assertTrue(r1.isFailure)
        val r2 = VolumeFindParser.parseDimension("--")
        assertTrue(r2.isFailure)
    }

    // 35. Empty input
    @Test
    fun test35_EmptyInput() {
        val r = VolumeFindParser.parseDimension("   ")
        assertTrue(r.isFailure)
    }

    // 36. Multiple decimal points
    @Test
    fun test36_MultipleDecimalPoints() {
        val r1 = VolumeFindParser.parseDimension("12..5")
        assertTrue(r1.isFailure)
        val r2 = VolumeFindParser.parseDimension("1.2.3")
        assertTrue(r2.isFailure)
    }

    // 37. Maximum input length
    @Test
    fun test37_MaximumInputLength() {
        val tooLong = "1".repeat(501)
        val r = VolumeFindParser.parseDimension(tooLong)
        assertTrue(r.isFailure)
        assertEquals("Number is too long. Maximum 500 digits.", r.exceptionOrNull()?.message)
    }

    // 38. HALF_UP
    @Test
    fun test38_RoundingHalfUp() {
        val v = BigDecimal("2.555")
        val res = VolumeFindFormatter.formatNumber(v, 2, RoundingMode.HALF_UP)
        assertEquals("2.56", res)
    }

    // 39. HALF_DOWN
    @Test
    fun test39_RoundingHalfDown() {
        val v = BigDecimal("2.555")
        val res = VolumeFindFormatter.formatNumber(v, 2, RoundingMode.HALF_DOWN)
        assertEquals("2.55", res)
    }

    // 40. HALF_EVEN
    @Test
    fun test40_RoundingHalfEven() {
        val v1 = BigDecimal("2.555")
        val res1 = VolumeFindFormatter.formatNumber(v1, 2, RoundingMode.HALF_EVEN)
        assertEquals("2.56", res1)

        val v2 = BigDecimal("2.545")
        val res2 = VolumeFindFormatter.formatNumber(v2, 2, RoundingMode.HALF_EVEN)
        assertEquals("2.54", res2)
    }

    // 41. UP
    @Test
    fun test41_RoundingUp() {
        val v = BigDecimal("2.551")
        val res = VolumeFindFormatter.formatNumber(v, 2, RoundingMode.UP)
        assertEquals("2.56", res)
    }

    // 42. DOWN
    @Test
    fun test42_RoundingDown() {
        val v = BigDecimal("2.559")
        val res = VolumeFindFormatter.formatNumber(v, 2, RoundingMode.DOWN)
        assertEquals("2.55", res)
    }

    // 43. CEILING
    @Test
    fun test43_RoundingCeiling() {
        val v = BigDecimal("2.551")
        val res = VolumeFindFormatter.formatNumber(v, 2, RoundingMode.CEILING)
        assertEquals("2.56", res)
    }

    // 44. FLOOR
    @Test
    fun test44_RoundingFloor() {
        val v = BigDecimal("2.559")
        val res = VolumeFindFormatter.formatNumber(v, 2, RoundingMode.FLOOR)
        assertEquals("2.55", res)
    }

    // 45. Trailing-zero formatting
    @Test
    fun test45_TrailingZeroFormatting() {
        val v1 = BigDecimal("125.000000")
        val res1 = VolumeFindFormatter.formatNumber(v1, 6, RoundingMode.HALF_UP)
        assertEquals("125", res1)

        val v2 = BigDecimal("125.500000")
        val res2 = VolumeFindFormatter.formatNumber(v2, 6, RoundingMode.HALF_UP)
        assertEquals("125.5", res2)
    }

    // 46. Shape selection
    @Test
    fun test46_ShapeSelection() {
        assertEquals(8, VolumeShape.entries.size)
        assertEquals("Cube", VolumeShape.CUBE.displayName)
        assertEquals(1, VolumeShape.CUBE.dimensionCount)
        assertEquals(3, VolumeShape.RECTANGULAR_PRISM.dimensionCount)
    }

    // 47. Shape switching
    @Test
    fun test47_ShapeSwitching() {
        val keysCube = VolumeFindValidator.getRequiredDimensionKeys(VolumeShape.CUBE)
        assertEquals(listOf("side"), keysCube)
        val keysCylinder = VolumeFindValidator.getRequiredDimensionKeys(VolumeShape.CYLINDER)
        assertEquals(listOf("radius", "height"), keysCylinder)
    }

    // 48. Unit search
    @Test
    fun test48_UnitSearch() {
        val resCm = LengthUnitRegistry.search("cm")
        assertTrue(resCm.any { it.unit == LengthUnit.CENTIMETER })

        val resFeet = LengthUnitRegistry.search("feet")
        assertTrue(resFeet.any { it.unit == LengthUnit.FOOT })

        val resInch = LengthUnitRegistry.search("in")
        assertTrue(resInch.any { it.unit == LengthUnit.INCH })
    }

    // 49. Unit selection
    @Test
    fun test49_UnitSelection() {
        val def = LengthUnitRegistry.get(LengthUnit.MILLIMETER)
        assertEquals("mm", def.symbol)
        assertEquals(VolumeUnit.CUBIC_MILLIMETER, LengthUnit.MILLIMETER.toVolumeUnit())
    }

    // 50. History save
    @Test
    fun test50_HistorySave() {
        val repo = VolumeFindHistoryRepository(FakeSharedPreferences())
        val saved = repo.saveCalculation(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to "5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            resultValue = "125 cm³",
            resultUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        assertTrue(saved)
        assertEquals(1, repo.historyFlow.value.size)
    }

    // 51. History load
    @Test
    fun test51_HistoryLoad() {
        val prefs = FakeSharedPreferences()
        val repo1 = VolumeFindHistoryRepository(prefs)
        repo1.saveCalculation(
            shape = VolumeShape.SPHERE,
            inputs = mapOf("radius" to "3"),
            inputUnits = mapOf("radius" to LengthUnit.CENTIMETER),
            resultValue = "113.097336 cm³",
            resultUnit = VolumeUnit.CUBIC_CENTIMETER
        )

        val repo2 = VolumeFindHistoryRepository(prefs)
        assertEquals(1, repo2.historyFlow.value.size)
        assertEquals(VolumeShape.SPHERE, repo2.historyFlow.value[0].shape)
    }

    // 52. History limit
    @Test
    fun test52_HistoryLimit() {
        val repo = VolumeFindHistoryRepository(FakeSharedPreferences())
        for (i in 1..15) {
            repo.saveCalculation(
                shape = VolumeShape.CUBE,
                inputs = mapOf("side" to "$i"),
                inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
                resultValue = "${i * i * i} cm³",
                resultUnit = VolumeUnit.CUBIC_CENTIMETER
            )
        }
        assertEquals(10, repo.historyFlow.value.size)
        assertEquals("15", repo.historyFlow.value.first().inputs["side"])
    }

    // 53. History deletion
    @Test
    fun test53_HistoryDeletion() {
        val repo = VolumeFindHistoryRepository(FakeSharedPreferences())
        repo.saveCalculation(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to "5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            resultValue = "125 cm³",
            resultUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        val id = repo.historyFlow.value.first().id
        repo.deleteItem(id)
        assertTrue(repo.historyFlow.value.isEmpty())
    }

    // 54. History clear
    @Test
    fun test54_HistoryClear() {
        val repo = VolumeFindHistoryRepository(FakeSharedPreferences())
        repo.saveCalculation(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to "5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            resultValue = "125 cm³",
            resultUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        repo.clearHistory()
        assertTrue(repo.historyFlow.value.isEmpty())
    }

    // 55. History restore
    @Test
    fun test55_HistoryRestore() {
        val historyItem = VolumeFindHistory(
            id = "test-id",
            shape = VolumeShape.CYLINDER,
            inputs = mapOf("radius" to "3", "height" to "10"),
            inputUnits = mapOf("radius" to LengthUnit.CENTIMETER, "height" to LengthUnit.CENTIMETER),
            resultValue = "282.743339 cm³",
            resultUnit = VolumeUnit.CUBIC_CENTIMETER,
            calculatedAt = System.currentTimeMillis()
        )
        val (parsedInputs, err) = VolumeFindValidator.validateShapeInputs(historyItem.shape, historyItem.inputs)
        assertNull(err)
        assertNotNull(parsedInputs)
        val (vol, _) = VolumeCalculator.calculateShapeVolume(
            shape = historyItem.shape,
            inputs = parsedInputs!!,
            inputUnits = historyItem.inputUnits,
            targetUnit = historyItem.resultUnit
        )
        val formatted = VolumeFindFormatter.formatVolume(vol, historyItem.resultUnit, 6, RoundingMode.HALF_UP)
        assertEquals(historyItem.resultValue, formatted)
    }

    // 56. Settings persistence
    @Test
    fun test56_SettingsPersistence() {
        val prefs = FakeSharedPreferences()
        val repo = SettingsRepository(prefs)
        repo.updateDecimalPlaces(4)
        repo.updateTheme(AppSettings.THEME_DARK)

        val reloaded = SettingsRepository(prefs)
        assertEquals(4, reloaded.settingsFlow.value.decimalPlaces)
        assertEquals(AppSettings.THEME_DARK, reloaded.settingsFlow.value.themeMode)
    }

    // 57. Last calculation persistence
    @Test
    fun test57_LastCalculationPersistence() {
        val prefs = FakeSharedPreferences()
        val repo = SettingsRepository(prefs)
        repo.saveLastCalculationState(
            shape = VolumeShape.CONE,
            inputs = mapOf("radius" to "4", "height" to "9"),
            inputUnits = mapOf("radius" to LengthUnit.CENTIMETER, "height" to LengthUnit.CENTIMETER),
            resultUnit = VolumeUnit.CUBIC_CENTIMETER
        )

        val loaded = repo.loadLastCalculationState()
        assertNotNull(loaded)
        assertEquals(VolumeShape.CONE, loaded!!.shape)
        assertEquals("4", loaded.inputs["radius"])
    }

    // 58. Reset
    @Test
    fun test58_Reset() {
        val defaultShape = VolumeShape.CUBE
        val defaultInputs = defaultShape.defaultDimensions
        val (vol, _) = VolumeCalculator.calculateShapeVolume(
            shape = defaultShape,
            inputs = mapOf("side" to BigDecimal("5")),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            targetUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        assertEquals("5", defaultInputs["side"])
        assertEquals(0, BigDecimal("125").compareTo(vol))
    }

    // 59. Duplicate history protection
    @Test
    fun test59_DuplicateHistoryProtection() {
        val repo = VolumeFindHistoryRepository(FakeSharedPreferences())
        val firstSave = repo.saveCalculation(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to "5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            resultValue = "125 cm³",
            resultUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        assertTrue(firstSave)

        val secondSave = repo.saveCalculation(
            shape = VolumeShape.CUBE,
            inputs = mapOf("side" to "5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            resultValue = "125 cm³",
            resultUnit = VolumeUnit.CUBIC_CENTIMETER
        )
        assertFalse(secondSave)
        assertEquals(1, repo.historyFlow.value.size)
    }

    // 60. Rapid input protection
    @Test
    fun test60_RapidInputProtection() {
        val rapidInputs = listOf("", "1", "1.", "1..", "1.5", "1.5.", "1.50", "abc", "-5", "0", "1E+2")
        for (input in rapidInputs) {
            val (v, _) = VolumeFindValidator.validateDimensionInput(input)
            // Should never crash
            if (v != null) {
                assertTrue(v > BigDecimal.ZERO)
            }
        }
    }
}
