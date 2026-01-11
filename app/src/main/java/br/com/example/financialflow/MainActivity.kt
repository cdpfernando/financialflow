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
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import br.com.example.financialflow.statement.StatementScreen
import br.com.example.financialflow.statement.TransactionItem
import br.com.example.financialflow.transactions.TransactionScreen
import br.com.example.financialflow.ui.theme.FinancialFlowTheme
import br.com.example.financialflow.transactions.TransactionViewModel
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = TransactionRepository(this)

        setContent {

            FinancialFlowTheme {

                val navController = rememberNavController()
                val viewModel: TransactionViewModel = viewModel()

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





@Preview(showBackground = true, name = "Transaction Screen")
@Composable
fun TransactionScreenPreview() {
    FinancialFlowTheme {
        TransactionScreen(
            repository = TransactionRepository(LocalContext.current),
            onNavigateToStatement = {},
            modifier = Modifier.padding(16.dp)
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