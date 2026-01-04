package br.com.example.financialflow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.example.financialflow.data.database.TransactionRepository
import br.com.example.financialflow.data.model.CreditDetail
import br.com.example.financialflow.data.model.DebitDetail
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import br.com.example.financialflow.ui.theme.FinancialFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = TransactionRepository(this)

        setContent {

            FinancialFlowTheme {

                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(
                            route = "home",
                            exitTransition = {
                                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                            },
                            popEnterTransition = {
                                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                            }
                        ) {
                            TransactionScreen(
                                onNavigateToStatement = {
                                    navController.navigate("statement")
                                }, modifier = Modifier.padding(16.dp),
                                repository = repository
                            )
                        }

                        composable(
                            route = "statement",
                            enterTransition = {
                                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                            },
                            popExitTransition = {
                                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                            }
                        ) {
                            StatementScreen(
                                modifier = Modifier,
                                repository = repository
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    repository: TransactionRepository,
    onNavigateToStatement: () -> Unit
//    viewModel: Transaction = viewModel(),
) {
    val context = LocalContext.current

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.DEBIT) }

    var isDebitDetailExpanded by remember { mutableStateOf(false) }
    var selectedDebitDetail by remember { mutableStateOf(DebitDetail.FOOD) }

    var isCreditDetailExpanded by remember { mutableStateOf(false) }
    var selectedCreditDetail by remember { mutableStateOf(CreditDetail.SALARY) }

    Column(
        modifier = modifier
    ) {
        Text(text = "Cadastro de Transação")

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text("Tipo:")
            RadioButton(
                selected = selectedType == TransactionType.DEBIT,
                onClick = { selectedType = TransactionType.DEBIT }
            )
            Text("Débito")
            RadioButton(
                selected = selectedType == TransactionType.CREDIT,
                onClick = { selectedType = TransactionType.CREDIT }
            )
            Text("Crédito")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedType == TransactionType.DEBIT) {
            ExposedDropdownMenuBox(
                expanded = isDebitDetailExpanded,
                onExpandedChange = { isDebitDetailExpanded = it }
            ) {
                TextField(
                    value = selectedDebitDetail.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Detalhe do Débito") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDebitDetailExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isDebitDetailExpanded,
                    onDismissRequest = { isDebitDetailExpanded = false }
                ) {
                    DebitDetail.values().forEach { detail ->
                        DropdownMenuItem(
                            text = { Text(detail.name) },
                            onClick = {
                                selectedDebitDetail = detail
                                isDebitDetailExpanded = false
                            }
                        )
                    }
                }
            }
        } else {
            ExposedDropdownMenuBox(
                expanded = isCreditDetailExpanded,
                onExpandedChange = { isCreditDetailExpanded = it }
            ) {
                TextField(
                    value = selectedCreditDetail.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Detalhe do Crédito") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCreditDetailExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isCreditDetailExpanded,
                    onDismissRequest = { isCreditDetailExpanded = false }
                ) {
                    CreditDetail.values().forEach { detail ->
                        DropdownMenuItem(
                            text = { Text(detail.name) },
                            onClick = {
                                selectedCreditDetail = detail
                                isCreditDetailExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Valor") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                if (selectedType == TransactionType.DEBIT) {
                    repository.addDebitTransaction(
                        amount = amountValue,
                        description = description,
                        debitDetail = selectedDebitDetail
                    )
                } else {
                    repository.addCreditTransaction(
                        amount = amountValue,
                        description = description,
                        creditDetail = selectedCreditDetail
                    )
                }
                Toast.makeText(context, "Transação salva!", Toast.LENGTH_SHORT).show()
                amount = ""
                description = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Transação")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
//            onClick = {
//                val transactions = repository.getAllTransactions()
//                val transactionsText = transactions.joinToString(separator = "\n") {
//                    val detail = when (it.type) {
//                        TransactionType.CREDIT -> it.creditDetail?.name
//                        TransactionType.DEBIT -> it.debitDetail?.name
//                    }
//                    "R$${it.amount} - ${it.description} (${it.type}) - $detail"
//                }
//                Toast.makeText(
//                    context,
//                    transactionsText.ifEmpty { "Nenhuma transação encontrada." },
//                    Toast.LENGTH_LONG
//                ).show()
//            },
            onClick = onNavigateToStatement,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Listar Transações")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val total = repository.getNetBalance()
                Toast.makeText(context, "Saldo total: R$${"%.2f".format(total)}", Toast.LENGTH_LONG)
                    .show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular Saldo Total")
        }
    }
}

@Composable
fun StatementScreen(
    modifier: Modifier = Modifier,
    repository: TransactionRepository
) {
    val (totalCredits, totalDebits) = remember { repository.getBalance() }
    val netBalance = remember { repository.getNetBalance() }
    val transactions = remember { repository.getAllTransactions() }

    Column(
        modifier = modifier.fillMaxSize()
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


            //INSERIR DATA DA TRANSAÇÃO
//            Text(
//                text = transaction.date
//            )

            Text(
                text = transaction.description,
                fontWeight = FontWeight.Bold
            )

            val detail = when (transaction.type) {
                TransactionType.CREDIT -> transaction.creditDetail?.name ?: ""
                TransactionType.DEBIT -> transaction.debitDetail?.name ?: ""
            }
            Text(
                text = detail,
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