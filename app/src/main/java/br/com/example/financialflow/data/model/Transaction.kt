package br.com.example.financialflow.data.model

import androidx.annotation.StringRes
import br.com.example.financialflow.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class TransactionType(@StringRes val descriptionResId: Int) {
    CREDIT(R.string.transaction_type_credit),
    DEBIT(R.string.transaction_type_debit)
}

enum class CreditDetail(@StringRes val descriptionResId: Int) {
    SALARY(R.string.credit_detail_salary),
    EXTRAS(R.string.credit_detail_extras)
}

enum class DebitDetail(@StringRes val descriptionResId: Int) {
    FOOD(R.string.debit_detail_food),
    TRANSPORT(R.string.debit_detail_transport),
    HEALTH(R.string.debit_detail_health),
    HOUSING(R.string.debit_detail_housing)
}

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val type: TransactionType,
    val creditDetail: CreditDetail? = null,
    val debitDetail: DebitDetail? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun getDisplayDate(): String {
        return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }

}