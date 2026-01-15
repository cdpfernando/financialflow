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
    val transactionToDelete: Transaction? = null // Novo estado para controlar o diálogo
)

class StatementViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository = TransactionRepository(application)

    private val _uiState = MutableStateFlow(StatementState())
    val uiState: StateFlow<StatementState> = _uiState.asStateFlow()

    fun refreshStatement() {
        viewModelScope.launch {
            val transactions = withContext(Dispatchers.IO) { repository.getAllTransactions() }
            val (credits, debits) = withContext(Dispatchers.IO) { repository.getBalance() }
            _uiState.update {
                it.copy(
                    transactions = transactions,
                    totalCredits = credits,
                    totalDebits = debits,
                    netBalance = credits - debits
                )
            }
        }
    }

    /**
     * Inicia o processo de exclusão, definindo qual transação está "em risco".
     * Isso fará com que a UI mostre o diálogo de confirmação.
     */
    fun onDeleteRequest(transaction: Transaction) {
        _uiState.update { it.copy(transactionToDelete = transaction) }
    }

    /**
     * Cancela a exclusão e esconde o diálogo.
     */
    fun onDeleteCancel() {
        _uiState.update { it.copy(transactionToDelete = null) }
    }

    /**
     * Confirma a exclusão, deleta do banco e atualiza a tela.
     */
    fun onDeleteConfirm() {
        _uiState.value.transactionToDelete?.let { transaction ->
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.deleteTransaction(transaction)
                }
                // Esconde o diálogo e recarrega os dados da fonte da verdade (o banco)
                _uiState.update { it.copy(transactionToDelete = null) }
                refreshStatement()
            }
        }
    }
}