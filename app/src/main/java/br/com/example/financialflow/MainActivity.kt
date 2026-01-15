package br.com.example.financialflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.example.financialflow.statement.StatementScreen
import br.com.example.financialflow.transactions.TransactionScreen
import br.com.example.financialflow.ui.theme.FinancialFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FinancialFlowTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "transaction",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(
                            route = "transaction",
                            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
                            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() }
                        ) {
                            TransactionScreen(
                                onNavigateToStatement = {
                                    navController.navigate("statement")
                                }
                            )
                        }

                        composable(
                            route = "statement",
                            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
                            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
                        ) {
                            StatementScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
