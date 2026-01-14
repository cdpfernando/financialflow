package br.com.example.financialflow.statement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import br.com.example.financialflow.ui.theme.FinancialFlowTheme
import java.time.LocalDateTime

@Composable
fun StatementScreen(
    modifier: Modifier = Modifier,
    viewModel: StatementViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.refreshStatement()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatementContent(
        modifier = modifier,
        state = uiState
    )
}

@Composable
fun StatementContent(
    modifier: Modifier = Modifier,
    state: StatementState
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Resumo Financeiro",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Entradas"
            )
            Text(
                text = "R$ ${"%.2f".format(state.totalCredits)}"
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Saídas"
            )
            Text(
                text = "R$ ${"%.2f".format(state.totalDebits)}",
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Saldo")
            Text(
                text = "R$ ${"%.2f".format(state.netBalance)}",
                color = if (state.netBalance >= 0) Color(0xFF008000) else Color.Red,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Histórico de Transações")

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(state.transactions) { transaction ->
                TransactionItem(transaction = transaction)
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = transaction.description,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = transaction.date,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        val amountColor =
            if (transaction.type == TransactionType.CREDIT) Color(0xFF008000) else Color.Red
        val amountPrefix = if (transaction.type == TransactionType.CREDIT) "+ " else "- "

        Text(
            text = "$amountPrefix R$ ${String.format("%.2f", transaction.amount)}",
            color = amountColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true, name = "Statement Screen with Data")
@Composable
fun StatementContentPreview() {
    FinancialFlowTheme {
        val previewState = StatementState(
            transactions = listOf(
                Transaction(
                    1,
                    1500.0,
                    "Salário",
                    TransactionType.CREDIT,
                    "01/09/2024",
                    LocalDateTime.now()
                ),
                Transaction(
                    2,
                    80.0,
                    "Supermercado",
                    TransactionType.DEBIT,
                    "02/09/2024",
                    LocalDateTime.now()
                ),
                Transaction(
                    3,
                    120.0,
                    "Restaurante",
                    TransactionType.DEBIT,
                    "02/09/2024",
                    LocalDateTime.now()
                )
            ),
            totalCredits = 1500.0,
            totalDebits = 200.0,
            netBalance = 1300.0
        )
        StatementContent(
            state = previewState,
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true, name = "Transaction Item - Credit")
@Composable
fun TransactionItemCreditPreview() {
    FinancialFlowTheme {
        TransactionItem(
            transaction = Transaction(
                id = 1,
                amount = 1200.50,
                description = "Salário",
                type = TransactionType.CREDIT,
                date = "01/09/2024",
                createdAt = LocalDateTime.now()
            )
        )
    }
}

@Preview(showBackground = true, name = "Transaction Item - Debit")
@Composable
fun TransactionItemDebitPreview() {
    FinancialFlowTheme {
        TransactionItem(
            transaction = Transaction(
                id = 2,
                amount = 75.25,
                description = "Compras",
                type = TransactionType.DEBIT,
                date = "02/09/2024",
                createdAt = LocalDateTime.now()
            )
        )
    }
}