package br.com.example.financialflow

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.example.financialflow.data.database.TransactionRepository
import br.com.example.financialflow.statement.StatementScreen
import br.com.example.financialflow.statement.StatementViewModel
import br.com.example.financialflow.transactions.TransactionScreen
import br.com.example.financialflow.transactions.TransactionViewModel
import br.com.example.financialflow.ui.theme.FinancialFlowTheme


class ViewModelFactory(
    private val application: Application,
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> {
                TransactionViewModel(application, repository) as T
            }
            modelClass.isAssignableFrom(StatementViewModel::class.java) -> {
                StatementViewModel(application, repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
class MainActivity : ComponentActivity() {
    private val repository by lazy { TransactionRepository(this) }
    private val viewModelFactory by lazy { ViewModelFactory(application, repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            FinancialFlowTheme {
                AppNavigationHost(factory = viewModelFactory)
            }
        }
    }
}

@Composable
private fun AppNavigationHost(factory: ViewModelProvider.Factory) {

    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "transaction_route",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = "transaction_route",
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeOut(
                        tween(700)
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeIn(
                        tween(700)
                    )
                }
            ) {

                val viewModel: TransactionViewModel = viewModel(factory = factory)

                TransactionScreen(
                    viewModel = viewModel,
                    onNavigateToStatement = {
                        navController.navigate("statement_route")
                    }
                )
            }

            composable(
                route = "statement_route",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeIn(
                        tween(700)
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeOut(
                        tween(700)
                    )
                }
            ) {

                val viewModel: StatementViewModel = viewModel(factory = factory)

                StatementScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}