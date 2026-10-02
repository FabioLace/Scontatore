package com.example.scontatore.domain

import java.math.BigDecimal

data class CalculationResult(
    val finalPrice: BigDecimal,
    val differenceAmount: BigDecimal,
)
