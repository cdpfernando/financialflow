package br.com.example.financialflow.statement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.example.financialflow.R
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import br.com.example.financialflow.ui.theme.FinancialFlowTheme
import java.time.LocalDateTime

@Composable
fun StatementScreen(
    modifier: Modifier = Modifier,
    viewModel: StatementViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.refreshStatement()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatementContent(
        modifier = modifier,
        state = uiState,
        onDeleteRequest = viewModel::onDeleteRequest,
        onDeleteConfirm = viewModel::onDeleteConfirm,
        onDeleteCancel = viewModel::onDeleteCancel,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun StatementContent(
    modifier: Modifier = Modifier,
    state: StatementState,
    onDeleteRequest: (Transaction) -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    onNavigateBack: () -> Unit
) {

    if (state.transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = onDeleteCancel,
            title = { Text(stringResource(R.string.title_confirmar_exclusao)) },
            text = { Text(stringResource(R.string.text_confirmar_exclusao)) },
            confirmButton = {
                TextButton(onClick = onDeleteConfirm) {
                    Text(stringResource(R.string.button_excluir))
                }
            },
            dismissButton = {
                TextButton(onClick = onDeleteCancel) {
                    Text(stringResource(R.string.button_cancelar))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(color = Color(0xFF4C5E8B))
        ) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.voltar),
                    tint = Color.White
                )
            }
            Text(
                text = stringResource(R.string.text_resumo_financeiro),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (state.isEmpty) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.text_nenhuma_transacao_encontrada),
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        } else {
            Column {
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp, 0.dp, 8.dp)
                        .background(color = Color(0xFFACBCE5))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.text_entradas),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp, 8.dp, 0.dp, 0.dp)
                        )
                        Text(
                            text = stringResource(R.string.text_rs, "%.2f".format(state.totalCredits)),
                            modifier = Modifier.padding(0.dp, 8.dp, 8.dp, 0.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.text_saidas),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
                        )
                        Text(
                            text = stringResource(R.string.text_rs, "%.2f".format(state.totalDebits)),
                            color = Color.Red,
                            modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.text_saldo),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.text_rs, "%.2f".format(state.netBalance)),
                            color = if (state.netBalance >= 0) Color(0xFF008000) else Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp, 0.dp, 8.dp)
                        .height(30.dp)
                        .background(color = Color(0xFF4C5E8B))
                ) {
                    Text(
                        text = stringResource(R.string.text_historico_de_transacoes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(state.transactions, key = { it.id }) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDismiss = { onDeleteRequest(transaction) }
                        )
                        Divider(color = Color.LightGray, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onDismiss: () -> Unit) {
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
        val amountPrefix = if (transaction.type == TransactionType.CREDIT) stringResource(R.string.plus_signal) else stringResource(
            R.string.minus_signal
        )

        Text(
            text = "$amountPrefix R$ ${String.format("%.2f", transaction.amount)}",
            color = amountColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.text_confirmar_exclusao),
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true, name = "Statement Screen Empty")
@Composable
fun StatementContentEmptyPreview() {
    FinancialFlowTheme {
        StatementContent(
            state = StatementState(isEmpty = true),
            modifier = Modifier,
            onDeleteRequest = {},
            onDeleteConfirm = {},
            onDeleteCancel = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Statement Screen with Dialog")
@Composable
fun StatementContentWithDialogPreview() {
    FinancialFlowTheme {
        val previewState = StatementState(
            transactions = listOf(
                Transaction(1, 1500.0, "Salário", TransactionType.CREDIT, "01/09/2024", LocalDateTime.now()),
                Transaction(2, 80.0, "Supermercado", TransactionType.DEBIT, "02/09/2024", LocalDateTime.now()),
            ),
            totalCredits = 1500.0,
            totalDebits = 80.0,
            netBalance = 1420.0,
            transactionToDelete = Transaction(2, 80.0, "Supermercado", TransactionType.DEBIT, "02/09/2024", LocalDateTime.now())
        )
        StatementContent(
            state = previewState,
            modifier = Modifier,
            onDeleteRequest = {},
            onDeleteConfirm = {},
            onDeleteCancel = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Statement Screen No Dialog")
@Composable
fun StatementContentNoDialogPreview() {
    FinancialFlowTheme {
        val previewState = StatementState(
            transactions = listOf(
                Transaction(1, 1500.0, "Salário", TransactionType.CREDIT, "01/09/2024", LocalDateTime.now()),
                Transaction(2, 80.0, "Supermercado", TransactionType.DEBIT, "02/09/2024", LocalDateTime.now()),
            ),
            totalCredits = 1500.0,
            totalDebits = 80.0,
            netBalance = 1420.0,
            transactionToDelete = null
        )
        StatementContent(
            state = previewState,
            modifier = Modifier,
            onDeleteRequest = {},
            onDeleteConfirm = {},
            onDeleteCancel = {},
            onNavigateBack = {}
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
                description = stringResource(R.string.description_salario),
                type = TransactionType.CREDIT,
                date = "01/09/2024",
                createdAt = LocalDateTime.now()
            )
        ) {}
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
                description = stringResource(R.string.description_compras),
                type = TransactionType.DEBIT,
                date = "02/09/2024",
                createdAt = LocalDateTime.now()
            )
        ) {}
    }
}
