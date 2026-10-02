package com.example.scontatore.domain

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class PercentageCalculatorTest {

    private fun calculate(originalPrice: String, percentage: String, mode: CalculationMode) =
        PercentageCalculator.calculate(BigDecimal(originalPrice), BigDecimal(percentage), mode)

    @Test
    fun `standard discount subtracts the percentage`() {
        val result = calculate("200", "15", CalculationMode.DISCOUNT)

        assertEquals(CalculationResult(BigDecimal("170.00"), BigDecimal("-30.00")), result)
    }

    @Test
    fun `standard increase adds the percentage`() {
        val result = calculate("200", "15", CalculationMode.INCREASE)

        assertEquals(CalculationResult(BigDecimal("230.00"), BigDecimal("30.00")), result)
    }

    @Test
    fun `zero percentage leaves the price unchanged`() {
        val expectedResult = CalculationResult(BigDecimal("200.00"), BigDecimal("0.00"))

        assertEquals(expectedResult, calculate("200", "0", CalculationMode.DISCOUNT))
        assertEquals(expectedResult, calculate("200", "0", CalculationMode.INCREASE))
    }

    @Test
    fun `full discount gives a final price of zero`() {
        val result = calculate("200", "100", CalculationMode.DISCOUNT)

        assertEquals(CalculationResult(BigDecimal("0.00"), BigDecimal("-200.00")), result)
    }

    @Test
    fun `discount above 100 percent is not valid`() {
        assertNull(calculate("200", "100.01", CalculationMode.DISCOUNT))
    }

    @Test
    fun `increase above 100 percent is valid`() {
        val result = calculate("200", "150", CalculationMode.INCREASE)

        assertEquals(CalculationResult(BigDecimal("500.00"), BigDecimal("300.00")), result)
    }

    @Test
    fun `negative values are not valid`() {
        assertNull(calculate("-200", "15", CalculationMode.DISCOUNT))
        assertNull(calculate("200", "-15", CalculationMode.INCREASE))
    }

    @Test
    fun `results are rounded half up to two decimals`() {
        assertEquals(
            CalculationResult(BigDecimal("66.66"), BigDecimal("-33.33")),
            calculate("99.99", "33.33", CalculationMode.DISCOUNT),
        )
        assertEquals(
            CalculationResult(BigDecimal("133.32"), BigDecimal("33.33")),
            calculate("99.99", "33.33", CalculationMode.INCREASE),
        )
        assertEquals(
            CalculationResult(BigDecimal("0.01"), BigDecimal("0.00")),
            calculate("0.01", "50", CalculationMode.DISCOUNT),
        )
    }

    @Test
    fun `very large numbers do not overflow`() {
        val result = calculate("99999999999999999999999999999999", "50", CalculationMode.INCREASE)

        assertEquals(BigDecimal("149999999999999999999999999999998.50"), result?.finalPrice)
    }

    @Test
    fun `input is parsed with comma or dot as decimal separator`() {
        assertEquals(BigDecimal("99.99"), PercentageCalculator.parseDecimalInput("99,99"))
        assertEquals(BigDecimal("99.99"), PercentageCalculator.parseDecimalInput("99.99"))
        assertEquals(BigDecimal("0.5"), PercentageCalculator.parseDecimalInput(",5"))
        assertNotNull(PercentageCalculator.parseDecimalInput("12."))
    }

    @Test
    fun `blank or malformed input is not parsed`() {
        listOf("", " ", ".", ",", "-5", "1.2.3", "1,2,3", "abc", "1e5").forEach { malformedInput ->
            assertNull(PercentageCalculator.parseDecimalInput(malformedInput), "input: \"$malformedInput\"")
        }
    }
}
