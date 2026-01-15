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
    val isDatePickerVisible: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val netBalanceToShow: Double? = null
)

class TransactionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: TransactionRepository = TransactionRepository(application)

    private val _uiState = MutableStateFlow(TransactionScreenState())
    val uiState: StateFlow<TransactionScreenState> = _uiState.asStateFlow()

    init {
        validateForm()
    }

    fun onAmountChange(input: String) {
        val amount = input.replace(',', '.')
        val regex = "^\\d*\\.?\\d{0,2}$".toRegex()
        if (amount.matches(regex)) {
            _uiState.update { it.copy(amount = amount) }
            validateForm()
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
        validateForm()
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
        validateForm()
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
        fun onCalculateBalance() {
        viewModelScope.launch {
            val balance = withContext(Dispatchers.IO) {
                repository.getNetBalance()
            }
            _uiState.update { it.copy(netBalanceToShow = balance) }
        }
    }

    fun onDismissBalanceModal() {
        _uiState.update { it.copy(netBalanceToShow = null) }
    }

    fun addTransaction() {
        if (!_uiState.value.isSaveEnabled) return

        viewModelScope.launch {
            val amountValue = _uiState.value.amount.toDoubleOrNull() ?: 0.0
            withContext(Dispatchers.IO) {
                repository.addTransaction(
                    amount = amountValue,
                    description = _uiState.value.description,
                    date = _uiState.value.date,
                    type = _uiState.value.selectedType
                )
            }
            resetScreen()
        }
    }

    private fun resetScreen() {
        _uiState.update {
            it.copy(
                amount = "",
                description = "",
                date = "",
                selectedDateMillis = null,
                isTransactionSaved = true
            )
        }
        validateForm()
    }

    private fun validateForm() {
        val state = _uiState.value
        val isAmountValid = (state.amount.toDoubleOrNull() ?: 0.0) > 0
        val isDescriptionValid = state.description.isNotBlank()
        val isDateValid = state.date.isNotBlank()
        _uiState.update { it.copy(isSaveEnabled = isAmountValid && isDescriptionValid && isDateValid) }
    }
}