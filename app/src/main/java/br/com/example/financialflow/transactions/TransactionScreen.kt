package br.com.example.financialflow.transactions

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.example.financialflow.data.model.TransactionType
import br.com.example.financialflow.ui.theme.FinancialFlowTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel,
    onNavigateToStatement: () -> Unit
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val maxLength = 20

    Column(
        modifier = modifier
    ) {
        Text(text = "Cadastro de Transação")

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text("Tipo:")
            RadioButton(
                selected = uiState.selectedType == TransactionType.DEBIT,
                onClick = { viewModel.onTypeChange(TransactionType.DEBIT) }
            )
            Text("Débito")
            RadioButton(
                selected = uiState.selectedType == TransactionType.CREDIT,
                onClick = { viewModel.onTypeChange(TransactionType.CREDIT) }
            )
            Text("Crédito")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = uiState.amount,
            onValueChange = { newAmount ->
                viewModel.onAmountChange(newAmount)
            },
            label = { Text("Valor") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = uiState.description,
            onValueChange = { newDescription ->
                if (newDescription.length <= maxLength) viewModel.onDescriptionChange(newDescription)
            },
            label = { Text("Descrição (máx 20 caracteres)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = uiState.date,
            onValueChange = { newDate -> viewModel.onDateChange(newDate) },
            label = { Text("Data") },
            modifier = Modifier.fillMaxWidth()
        )

//        var showDatePicker by remember { mutableStateOf(false) }
//        val datePickerState = rememberDatePickerState(
//            initialSelectedDateMillis = uiState.date
//        )
//
//        val formattedDate = uiState.date?.let {
//            SimpleDateFormat(
//                "dd/MM/yyyy", Locale.getDefault()
//            ).format(Date(it))
//        } ?: ""
//
//        TextField(
//            value = formattedDate,
//            onValueChange = {},
//            label = { Text("Data da Transação") },
//            readOnly = true,
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable { showDatePicker = true }
//        )
//
//        if (showDatePicker) {
//            DatePickerDialog(
//                onDismissRequest = {
//                    showDatePicker = false
//                },
//                confirmButton = {
//                    TextButton(
//                        onClick = {
//                            datePickerState.selectedDateMillis?.let {
//                                viewModel.onDateChange(it)
//                            }
//                            showDatePicker = false
//                        }
//                    ) {
//                        Text("OK")
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showDatePicker = false }) {
//                        Text("Cancelar")
//                    }
//                }
//            ) {
//                DatePicker(state = datePickerState)
//            }
//        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.addTransaction()
                Toast.makeText(context, "Transação salva!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Transação")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToStatement,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Listar Transações")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val total = viewModel.getNetBalance()
                Toast.makeText(context, "Saldo total: R$${"%.2f".format(total)}", Toast.LENGTH_LONG)
                    .show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular Saldo Total")
        }
    }
}

@Preview(showBackground = true, name = "Transaction Screen")
@Composable
fun TransactionScreenPreview() {
    FinancialFlowTheme {
        TransactionScreen(
            onNavigateToStatement = {},
            modifier = Modifier.padding(16.dp),
            viewModel = viewModel()
        )
    }
}