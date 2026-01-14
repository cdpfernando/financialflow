package br.com.example.financialflow.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.example.financialflow.data.database.TransactionRepository
import br.com.example.financialflow.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class TransactionScreenState(
    val amount: String = "",
    val description: String = "",
    val date: String = "",
    val selectedType: TransactionType = TransactionType.DEBIT,
    val isTransactionSaved: Boolean = false,
    val selectedDateMillis: Long? = null,
    val isDatePickerVisible: Boolean = false
)

class TransactionViewModel(
    application: Application
) : AndroidViewModel(application) {

    //TODO: Adicionar container de DI
    private val repository: TransactionRepository = TransactionRepository(application)
    private val _uiState = MutableStateFlow(TransactionScreenState())
    val uiState: StateFlow<TransactionScreenState> = _uiState.asStateFlow()

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onDateClick() {
        _uiState.update { it.copy(isDatePickerVisible = true) }
    }

    fun onDateSelected(millis: Long?) {
        _uiState.update { currentState ->
            val formattedDate = if (millis != null) {
                val instant = Instant.ofEpochMilli(millis)
                val date = LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate()
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } else {
                currentState.date
            }
            currentState.copy(
                selectedDateMillis = millis,
                isDatePickerVisible = false,
                date = formattedDate
            )
        }
    }

    fun onDismissDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = false) }
    }

    fun onTypeChange(type: TransactionType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onTransactionSavedHandled() {
        _uiState.update { it.copy(isTransactionSaved = false) }
    }

    fun addTransaction() {
        viewModelScope.launch {
            val amountValue = _uiState.value.amount.toDoubleOrNull() ?: 0.0
            if (amountValue > 0) {
                withContext(Dispatchers.IO) {
                    repository.addTransaction(
                        amount = amountValue,
                        description = _uiState.value.description,
                        date = _uiState.value.date,
                        type = _uiState.value.selectedType
                    )
                }
                _uiState.update {
                    it.copy(
                        amount = "",
                        description = "",
                        date = "",
                        isTransactionSaved = true
                    )
                }
            }
        }
    }

    suspend fun getNetBalance(): Double {
        return withContext(Dispatchers.IO) {
            repository.getNetBalance()
        }
    }
}