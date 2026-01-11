package br.com.example.financialflow.transactions

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.example.financialflow.data.database.TransactionRepository
import br.com.example.financialflow.data.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    repository: TransactionRepository,
    onNavigateToStatement: () -> Unit
) {
    val context = LocalContext.current

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.DEBIT) }

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

        TextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Data") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                repository.addTransaction(
                    amount = amountValue,
                    description = description,
                    date = date,
                    type = selectedType
                )

                Toast.makeText(context, "Transação salva!", Toast.LENGTH_SHORT).show()
                amount = ""
                description = ""
                date = ""
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