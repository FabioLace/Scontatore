package com.example.scontatore.ui

import com.example.scontatore.domain.CalculationMode
import com.example.scontatore.domain.CalculationResult

data class CalculatorUiState(
    val calculationMode: CalculationMode = CalculationMode.DISCOUNT,
    val originalPriceInput: String = "",
    val percentageInput: String = "",
    val calculationResult: CalculationResult? = null,
    val isDiscountOverLimit: Boolean = false,
)
