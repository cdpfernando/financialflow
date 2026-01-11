package br.com.example.financialflow.data.model

import androidx.annotation.StringRes
import br.com.example.financialflow.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class TransactionType(@StringRes val descriptionResId: Int) {
    CREDIT(R.string.transaction_type_credit),
    DEBIT(R.string.transaction_type_debit)
}

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val type: TransactionType,
    val date: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun getDisplayDate(): String {
        return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }

}