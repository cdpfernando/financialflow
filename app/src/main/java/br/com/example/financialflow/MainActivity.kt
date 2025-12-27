package br.com.example.financialflow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.example.financialflow.data.database.TransactionRepository
import br.com.example.financialflow.data.model.CreditDetail
import br.com.example.financialflow.data.model.DebitDetail
import br.com.example.financialflow.data.model.TransactionType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = TransactionRepository(this)

        setContent {
            TransactionScreen(
                modifier = Modifier.padding(16.dp),
                repository = repository
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(modifier: Modifier = Modifier, repository: TransactionRepository) {
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
            onClick = {
                val transactions = repository.getAllTransactions()
                val transactionsText = transactions.joinToString(separator = "\n") {
                    val detail = when (it.type) {
                        TransactionType.CREDIT -> it.creditDetail?.name
                        TransactionType.DEBIT -> it.debitDetail?.name
                    }
                    "R$${it.amount} - ${it.description} (${it.type}) - $detail"
                }
                Toast.makeText(context, transactionsText.ifEmpty { "Nenhuma transação encontrada." }, Toast.LENGTH_LONG).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Listar Transações (Toast)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val total = repository.getNetBalance()
                Toast.makeText(context, "Saldo total: R$${"%.2f".format(total)}", Toast.LENGTH_LONG).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular Saldo Total")
        }
    }
}
