package br.com.example.financialflow.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.example.financialflow.data.database.TransactionRepository
import br.com.example.financialflow.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionScreenState(
    val amount: String = "",
    val description: String = "",
    val date: String = "",
    val selectedType: TransactionType = TransactionType.DEBIT,
    val isTransactionSaved: Boolean = false
)
class TransactionViewModel(
    application: Application,
    private val repository: TransactionRepository = TransactionRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TransactionScreenState())
    val uiState: StateFlow<TransactionScreenState> = _uiState.asStateFlow()

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onDateChange(date: String) {
        _uiState.update { it.copy(date = date) }
    }

    fun onTypeChange(type: TransactionType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun addTransaction() {
        viewModelScope.launch {
            val amountValue = _uiState.value.amount.toDoubleOrNull() ?: 0.0

            repository.addTransaction(
                amount = amountValue,
                description = _uiState.value.description,
                date = _uiState.value.date,
                type = _uiState.value.selectedType
            )
            _uiState.update {
                it.copy(
                    amount = "",
                    description = "",
                    date = "",
                    isTransactionSaved = true
                )
            }
        }

        fun onTransactionSavedHandled() {
            _uiState.update { it.copy(isTransactionSaved = false) }
        }
    }
}