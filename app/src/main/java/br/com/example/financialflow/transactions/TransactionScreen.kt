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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
        onDateChange = viewModel::onDateChange,
        onTypeChange = viewModel::onTypeChange,
        onAddTransaction = viewModel::addTransaction,
        onNavigateToStatement = onNavigateToStatement,
        onCalculateBalance = {
            scope.launch {
                val total = viewModel.getNetBalance()
                Toast.makeText(context, "Saldo total: R$${"%.2f".format(total)}", Toast.LENGTH_LONG).show()
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
    onDateChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onAddTransaction: () -> Unit,
    onNavigateToStatement: () -> Unit,
    onCalculateBalance: () -> Unit
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

        TextField(
            value = uiState.date,
            onValueChange = onDateChange,
            label = { Text("Data (dd/MM/yyyy)") },
            modifier = Modifier.fillMaxWidth()
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


@Preview(showBackground = true, name = "Transaction Screen")
@Composable
fun TransactionScreenPreview() {
    FinancialFlowTheme {
        TransactionScreenContent(
            modifier = Modifier.padding(16.dp),
            uiState = TransactionScreenState(
                amount = "123.45",
                description = "cafezinho",
                date = "10/10/2024"
            ),
            onAmountChange = {},
            onDescriptionChange = {},
            onDateChange = {},
            onTypeChange = {},
            onAddTransaction = {},
            onNavigateToStatement = {},
            onCalculateBalance = {}
        )
    }
}
