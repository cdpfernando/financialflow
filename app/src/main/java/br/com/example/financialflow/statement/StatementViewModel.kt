package br.com.example.financialflow.statement

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.example.financialflow.data.database.TransactionRepository
import br.com.example.financialflow.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StatementState(
    val transactions: List<Transaction> = emptyList(),
    val totalCredits: Double = 0.0,
    val totalDebits: Double = 0.0,
    val netBalance: Double = 0.0,
    val isEmpty: Boolean = false
)

class StatementViewModel(
    application: Application,
    private val repository: TransactionRepository = TransactionRepository(application)
) : AndroidViewModel(application) {


    private val _uiState = MutableStateFlow(StatementState())
    val uiState: StateFlow<StatementState> = _uiState.asStateFlow()

    fun refreshStatement() {
        viewModelScope.launch {
            val transactions = repository.getAllTransactions()
            val (credits, debits) = repository.getBalance()
            _uiState.update { currentState ->
                currentState.copy(
                    transactions = transactions,
                    totalCredits = credits,
                    totalDebits = debits,
                    netBalance = credits - debits
                )
            }
        }
    }
}
