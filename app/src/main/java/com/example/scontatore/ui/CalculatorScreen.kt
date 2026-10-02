package com.example.scontatore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scontatore.R
import com.example.scontatore.domain.CalculationMode
import com.example.scontatore.domain.CalculationResult
import com.example.scontatore.ui.theme.OnSaleYellow
import com.example.scontatore.ui.theme.SaleYellow
import com.example.scontatore.ui.theme.ScontatoreTheme
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    calculatorViewModel: CalculatorViewModel = viewModel(),
) {
    val uiState by calculatorViewModel.uiState.collectAsStateWithLifecycle()

    CalculatorContent(
        uiState = uiState,
        onCalculationModeSelected = calculatorViewModel::onCalculationModeSelected,
        onOriginalPriceChanged = calculatorViewModel::onOriginalPriceChanged,
        onPercentageChanged = calculatorViewModel::onPercentageChanged,
        onReset = calculatorViewModel::onReset,
        modifier = modifier,
    )
}

@Composable
private fun CalculatorContent(
    uiState: CalculatorUiState,
    onCalculationModeSelected: (CalculationMode) -> Unit,
    onOriginalPriceChanged: (String) -> Unit,
    onPercentageChanged: (String) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.widthIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                CalculationModeSelector(
                    selectedMode = uiState.calculationMode,
                    onCalculationModeSelected = onCalculationModeSelected,
                )
                DecimalInputField(
                    value = uiState.originalPriceInput,
                    onValueChange = onOriginalPriceChanged,
                    label = stringResource(R.string.original_price_label),
                    imeAction = ImeAction.Next,
                )
                DecimalInputField(
                    value = uiState.percentageInput,
                    onValueChange = onPercentageChanged,
                    label = stringResource(R.string.percentage_label),
                    imeAction = ImeAction.Done,
                    suffix = stringResource(R.string.percentage_suffix),
                    errorMessage = if (uiState.isDiscountOverLimit) {
                        stringResource(R.string.discount_over_100_error)
                    } else {
                        null
                    },
                )
                ResultCard(
                    calculationResult = uiState.calculationResult,
                    calculationMode = uiState.calculationMode,
                )
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    Text(text = stringResource(R.string.reset_button))
                }
            }
        }
    }
}

@Composable
private fun CalculationModeSelector(
    selectedMode: CalculationMode,
    onCalculationModeSelected: (CalculationMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        CalculationMode.entries.forEachIndexed { index, calculationMode ->
            SegmentedButton(
                selected = calculationMode == selectedMode,
                onClick = { onCalculationModeSelected(calculationMode) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = CalculationMode.entries.size,
                ),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                icon = {
                    Icon(
                        painter = painterResource(
                            when (calculationMode) {
                                CalculationMode.DISCOUNT -> R.drawable.ic_trending_down
                                CalculationMode.INCREASE -> R.drawable.ic_trending_up
                            },
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                    )
                },
            ) {
                Text(
                    text = stringResource(
                        when (calculationMode) {
                            CalculationMode.DISCOUNT -> R.string.mode_discount
                            CalculationMode.INCREASE -> R.string.mode_increase
                        },
                    ),
                )
            }
        }
    }
}

@Composable
private fun DecimalInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    errorMessage: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.titleLarge,
        label = { Text(text = label) },
        suffix = suffix?.let { suffixText -> { Text(text = suffixText) } },
        isError = errorMessage != null,
        supportingText = errorMessage?.let { message -> { Text(text = message) } },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction,
        ),
    )
}

@Composable
private fun ResultCard(
    calculationResult: CalculationResult?,
    calculationMode: CalculationMode,
    modifier: Modifier = Modifier,
) {
    val deviceLocale = LocalConfiguration.current.locales[0]
    val amountFormat = remember(deviceLocale) {
        NumberFormat.getNumberInstance(deviceLocale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            roundingMode = RoundingMode.HALF_UP
        }
    }
    val missingAmountPlaceholder = stringResource(R.string.missing_amount_placeholder)
    val formattedFinalPrice = calculationResult?.let { amountFormat.format(it.finalPrice) }
        ?: missingAmountPlaceholder
    val formattedOriginalPrice = calculationResult?.let {
        amountFormat.format(it.finalPrice - it.differenceAmount)
    }
    val formattedDifference = calculationResult?.differenceAmount?.let { differenceAmount ->
        val formattedAmount = amountFormat.format(differenceAmount)
        if (differenceAmount.signum() > 0) "+$formattedAmount" else formattedAmount
    } ?: missingAmountPlaceholder

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.final_price_label),
                    style = MaterialTheme.typography.labelLarge,
                )
                formattedOriginalPrice?.let { originalPriceText ->
                    Text(
                        text = originalPriceText,
                        color = LocalContentColor.current.copy(alpha = 0.7f),
                        textDecoration = TextDecoration.LineThrough,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            Text(
                text = formattedFinalPrice,
                color = SaleYellow,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(minFontSize = 20.sp, maxFontSize = 57.sp),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFeatureSettings = "tnum",
                ),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        when (calculationMode) {
                            CalculationMode.DISCOUNT -> R.string.discount_amount_label
                            CalculationMode.INCREASE -> R.string.increase_amount_label
                        },
                    ),
                    style = MaterialTheme.typography.labelLarge,
                )
                if (calculationResult != null) {
                    DifferenceBadge(formattedDifference = formattedDifference)
                } else {
                    Text(
                        text = formattedDifference,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun DifferenceBadge(
    formattedDifference: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = SaleYellow,
        contentColor = OnSaleYellow,
    ) {
        Text(
            text = formattedDifference,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontFeatureSettings = "tnum",
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CalculatorContentPreview() {
    ScontatoreTheme {
        CalculatorContent(
            uiState = CalculatorUiState(
                originalPriceInput = "200",
                percentageInput = "15",
                calculationResult = CalculationResult(
                    finalPrice = BigDecimal("170.00"),
                    differenceAmount = BigDecimal("-30.00"),
                ),
            ),
            onCalculationModeSelected = {},
            onOriginalPriceChanged = {},
            onPercentageChanged = {},
            onReset = {},
        )
    }
}
