package br.com.example.financialflow.transactions

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
            Toast.makeText(context, "Transação salva!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Saldo total: R$${"%.2f".format(total)}", Toast.LENGTH_LONG)
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
        Text(text = "Cadastro de Transação")

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text("Tipo:")
            RadioButton(
                selected = uiState.selectedType == TransactionType.DEBIT,
                onClick = { onTypeChange(TransactionType.DEBIT) }
            )
            Text("Débito")
            RadioButton(
                selected = uiState.selectedType == TransactionType.CREDIT,
                onClick = { onTypeChange(TransactionType.CREDIT) }
            )
            Text("Crédito")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = uiState.amount,
            onValueChange = onAmountChange,
            label = { Text("Valor") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text("Descrição") },
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Transação")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToStatement,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Extrato")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onCalculateBalance,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular Saldo Total")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String = "Data",
    selectedDateMillis: Long?,
    isDatePickerVisible: Boolean,
    onDateFieldClick: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    onDismissDatePicker: () -> Unit
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
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )
        DatePickerDialog(
            onDismissRequest = onDismissDatePicker,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDatePicker) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box {
        TextField(
            value = text,
            onValueChange = { },
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
                description = "Salario",
                date = "10/10/2024"
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
