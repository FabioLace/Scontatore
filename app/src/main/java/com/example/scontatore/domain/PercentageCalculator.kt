package com.example.scontatore.domain

import java.math.BigDecimal
import java.math.RoundingMode

/** Pure calculation logic, independent from Android. */
object PercentageCalculator {

    val MAX_DISCOUNT_PERCENTAGE: BigDecimal = BigDecimal(100)

    private const val RESULT_SCALE = 2
    private val unsignedDecimalPattern = Regex("""\d+([.,]\d*)?|[.,]\d+""")

    /**
     * Parses a non-negative decimal number written with either a dot or a comma
     * as decimal separator. Returns null when the input is blank or malformed.
     */
    fun parseDecimalInput(input: String): BigDecimal? {
        val trimmedInput = input.trim()
        if (!unsignedDecimalPattern.matches(trimmedInput)) return null
        return trimmedInput.replace(',', '.').toBigDecimalOrNull()
    }

    /**
     * Returns null when the values are not valid for the given mode:
     * negative amounts, or a discount above 100%.
     */
    fun calculate(
        originalPrice: BigDecimal,
        percentage: BigDecimal,
        mode: CalculationMode,
    ): CalculationResult? {
        if (originalPrice.signum() < 0 || percentage.signum() < 0) return null
        if (isDiscountOverLimit(percentage, mode)) return null

        val percentageFraction = percentage.movePointLeft(2)
        val priceMultiplier = when (mode) {
            CalculationMode.DISCOUNT -> BigDecimal.ONE - percentageFraction
            CalculationMode.INCREASE -> BigDecimal.ONE + percentageFraction
        }
        val finalPrice = (originalPrice * priceMultiplier).setScale(RESULT_SCALE, RoundingMode.HALF_UP)
        val differenceAmount = (finalPrice - originalPrice).setScale(RESULT_SCALE, RoundingMode.HALF_UP)

        return CalculationResult(finalPrice = finalPrice, differenceAmount = differenceAmount)
    }

    fun isDiscountOverLimit(percentage: BigDecimal, mode: CalculationMode): Boolean =
        mode == CalculationMode.DISCOUNT && percentage > MAX_DISCOUNT_PERCENTAGE
}
