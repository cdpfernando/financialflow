package br.com.example.financialflow.statement

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.example.financialflow.data.database.TransactionRepository
import br.com.example.financialflow.data.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class StatementState(
    val transactions: List<Transaction> = emptyList(),
    val totalCredits: Double = 0.0,
    val totalDebits: Double = 0.0,
    val netBalance: Double = 0.0,
    val transactionToDelete: Transaction? = null,
    val isEmpty: Boolean = true
)

class StatementViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository = TransactionRepository(application)

    private val _uiState = MutableStateFlow(StatementState())
    val uiState: StateFlow<StatementState> = _uiState.asStateFlow()

    fun onDeleteRequest(transaction: Transaction) {
        _uiState.update { it.copy(transactionToDelete = transaction) }
    }

    fun onDeleteCancel() {
        _uiState.update { it.copy(transactionToDelete = null) }
    }

    fun onDeleteConfirm() {
        _uiState.value.transactionToDelete?.let { transaction ->
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.deleteTransaction(transaction)
                }
                _uiState.update { it.copy(transactionToDelete = null) }
                refreshStatement()
            }
        }
    }

    fun refreshStatement() {
        viewModelScope.launch {
            val transactions = withContext(Dispatchers.IO) { repository.getAllTransactions() }
            val (credits, debits) = withContext(Dispatchers.IO) { repository.getBalance() }

            _uiState.update { currentState ->
                currentState.copy(
                    transactions = transactions,
                    totalCredits = credits,
                    totalDebits = debits,
                    netBalance = credits - debits,
                    isEmpty = transactions.isEmpty()
                )
            }
        }
    }
}