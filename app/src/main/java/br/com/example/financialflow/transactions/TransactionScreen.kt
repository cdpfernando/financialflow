package br.com.example.financialflow.transactions

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.example.financialflow.R
import br.com.example.financialflow.data.model.TransactionType
import br.com.example.financialflow.ui.theme.FinancialFlowTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = viewModel(),
    onNavigateToStatement: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isTransactionSaved) {
        if (uiState.isTransactionSaved) {
            Toast.makeText(
                context,
                context.getString(R.string.text_toast_transacao_salva), Toast.LENGTH_SHORT
            ).show()
            viewModel.onTransactionSavedHandled()
        }
    }

    TransactionScreenContent(
        modifier = modifier,
        uiState = uiState,
        onAmountChange = viewModel::onAmountChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onDateFieldClick = viewModel::onDateClick,
        onDateSelected = viewModel::onDateSelected,
        onDismissDatePicker = viewModel::onDismissDatePicker,
        onTypeChange = viewModel::onTypeChange,
        onAddTransaction = viewModel::addTransaction,
        onNavigateToStatement = onNavigateToStatement,
        onCalculateBalance = {
            scope.launch {
                val total = viewModel.getNetBalance()
                Toast.makeText(
                    context,
                    context.getString(R.string.text_toast_saldo_total_rs, "%.2f".format(total)),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    )
}

@Composable
fun TransactionScreenContent(
    modifier: Modifier = Modifier,
    uiState: TransactionScreenState,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateFieldClick: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    onDismissDatePicker: () -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onAddTransaction: () -> Unit,
    onNavigateToStatement: () -> Unit,
    onCalculateBalance: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp, 0.dp, 8.dp)
                .height(40.dp)
                .background(color = Color(0xFF4C5E8B))
                .wrapContentSize(align = Alignment.Center)
        ) {
            Text(
                text = stringResource(R.string.text_cadastro_de_transacao),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        )
        {
            Text(
                stringResource(R.string.text_tipo)
            )
            RadioButton(
                selected = uiState.selectedType == TransactionType.DEBIT,
                onClick = { onTypeChange(TransactionType.DEBIT) }
            )
            Text(
                stringResource(R.string.transaction_type_debit),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            RadioButton(
                selected = uiState.selectedType == TransactionType.CREDIT,
                onClick = { onTypeChange(TransactionType.CREDIT) }
            )
            Text(
                stringResource(R.string.transaction_type_credit),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = uiState.amount,
            onValueChange = onAmountChange,
            label = { Text(stringResource(R.string.label_valor)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.label_descricao)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        DatePickerField(
            label = "Data",
            selectedDateMillis = uiState.selectedDateMillis,
            isDatePickerVisible = uiState.isDatePickerVisible,
            onDateFieldClick = onDateFieldClick,
            onDateSelected = onDateSelected,
            onDismissDatePicker = onDismissDatePicker
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddTransaction,
            enabled = uiState.isSaveEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.text_salvar_transacao))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToStatement,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.text_ver_extrato))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onCalculateBalance,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.text_calcular_saldo_total))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDateMillis: Long?,
    isDatePickerVisible: Boolean,
    onDateFieldClick: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    onDismissDatePicker: () -> Unit,
) {
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val text = remember(selectedDateMillis) {
        selectedDateMillis?.let { dateFormatter.format(Date(it)) } ?: ""
    }

    if (isDatePickerVisible) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = onDismissDatePicker,
            confirmButton = {
                TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDatePicker) {
                    Text(stringResource(R.string.text_cancelar))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box {
        TextField(
            value = text,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDateFieldClick() }
        )
    }
}

@Preview(showBackground = true, name = "Transaction Screen")
@Composable
fun TransactionScreenPreview() {
    FinancialFlowTheme {
        TransactionScreenContent(
            modifier = Modifier.padding(16.dp),
            uiState = TransactionScreenState(
                amount = "123.45",
                description = stringResource(R.string.description_salario),
                date = "10/10/2024",
                isSaveEnabled = true
            ),
            onAmountChange = {},
            onDescriptionChange = {},
            onTypeChange = {},
            onAddTransaction = {},
            onNavigateToStatement = {},
            onCalculateBalance = {},
            onDateFieldClick = {},
            onDateSelected = {},
            onDismissDatePicker = {}
        )
    }
}
