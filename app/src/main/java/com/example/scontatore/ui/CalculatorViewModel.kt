package com.example.scontatore.ui

import androidx.lifecycle.ViewModel
import com.example.scontatore.domain.CalculationMode
import com.example.scontatore.domain.PercentageCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {

    private val mutableUiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = mutableUiState.asStateFlow()

    fun onCalculationModeSelected(calculationMode: CalculationMode) {
        mutableUiState.update { it.copy(calculationMode = calculationMode).recalculated() }
    }

    fun onOriginalPriceChanged(originalPriceInput: String) {
        if (!isAcceptedInput(originalPriceInput)) return
        mutableUiState.update { it.copy(originalPriceInput = originalPriceInput).recalculated() }
    }

    fun onPercentageChanged(percentageInput: String) {
        if (!isAcceptedInput(percentageInput)) return
        mutableUiState.update { it.copy(percentageInput = percentageInput).recalculated() }
    }

    fun onReset() {
        mutableUiState.update { CalculatorUiState(calculationMode = it.calculationMode) }
    }

    private fun isAcceptedInput(input: String): Boolean =
        input.length <= MAX_INPUT_LENGTH && partialDecimalPattern.matches(input)

    private fun CalculatorUiState.recalculated(): CalculatorUiState {
        val originalPrice = PercentageCalculator.parseDecimalInput(originalPriceInput)
        val percentage = PercentageCalculator.parseDecimalInput(percentageInput)
        return copy(
            calculationResult = if (originalPrice != null && percentage != null) {
                PercentageCalculator.calculate(originalPrice, percentage, calculationMode)
            } else {
                null
            },
            isDiscountOverLimit = percentage != null &&
                PercentageCalculator.isDiscountOverLimit(percentage, calculationMode),
        )
    }

    private companion object {
        const val MAX_INPUT_LENGTH = 15

        /** Digits with at most one separator: rejects the minus sign and anything else while typing. */
        val partialDecimalPattern = Regex("""\d*[.,]?\d*""")
    }
}
