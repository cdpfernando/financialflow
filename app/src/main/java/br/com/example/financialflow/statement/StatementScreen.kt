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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.example.financialflow.data.database.TransactionRepository
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import br.com.example.financialflow.ui.theme.FinancialFlowTheme
import java.time.LocalDateTime


@Composable
fun StatementScreen(
    modifier: Modifier = Modifier,
    repository: TransactionRepository
) {
    val (totalCredits, totalDebits) = remember { repository.getBalance() }
    val netBalance = remember { repository.getNetBalance() }
    val transactions = remember { repository.getAllTransactions() }


    Column(
        modifier = modifier.padding(16.dp).fillMaxSize()
    ) {
        Text(
            text = "Resumo Financeiro",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Entradas"
            )
            Text(
                text = "R$ ${"%.2f".format(totalCredits)}"
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Saídas"
            )
            Text(
                text = "R$ ${"%.2f".format(totalDebits)}",
                color = Color.Red
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Saldo"
            )
            Text(
                text = "R$ ${"%.2f".format(netBalance)}",
                color = if (netBalance >= 0) Color(0xFF008000) else Color.Red
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Histórico de Transações"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LazyColumn(
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction = transaction)
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: br.com.example.financialflow.data.model.Transaction) {
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
            val type = when (transaction.type) {
                TransactionType.CREDIT -> "C"
                TransactionType.DEBIT -> "D"
            }
            Text(
                text = type,
                fontSize = 12.sp,
                color = if (transaction.type == TransactionType.CREDIT) Color(0xFF008000) else Color.Red,
            )

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

        Text(
            text = "R$ ${"%.2f".format(transaction.amount)}",
            color = if (transaction.type == TransactionType.CREDIT) Color(0xFF008000) else Color.Red,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
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
                description = "Salary",
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
                description = "Groceries",
                type = TransactionType.DEBIT,
                date = "02/09/2024",
                createdAt = LocalDateTime.now()
            )
        )
    }
}