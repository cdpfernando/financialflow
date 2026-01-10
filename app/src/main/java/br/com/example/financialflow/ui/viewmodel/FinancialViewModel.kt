package br.com.example.financialflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.example.financialflow.data.database.TransactionRepository
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FinancialSummaryState(
    val transactions: List<Transaction> = emptyList(),
    val totalCredits: Double = 0.0,
    val totalDebits: Double = 0.0,
    val netBalance: Double = 0.0
)

class FinancialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TransactionRepository(application)

    private val _uiState = MutableStateFlow(FinancialSummaryState())
    val uiState: StateFlow<FinancialSummaryState> = _uiState.asStateFlow()

    init {
        refreshFinancialSummary()
    }

    fun addTransaction(amount: Double, description: String, date: String, type: TransactionType) {
        viewModelScope.launch {
            repository.addTransaction(
                amount = amount,
                description = description,
                date = date,
                type = type
            )
            refreshFinancialSummary()
        }
    }

    fun refreshFinancialSummary() {
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
