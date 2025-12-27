package br.com.example.financialflow.data.database

import android.content.Context
import br.com.example.financialflow.data.model.CreditDetail
import br.com.example.financialflow.data.model.DebitDetail
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType

class TransactionRepository(context : Context) {
    private val database: AppDatabase = AppDatabase.getInstance(context = context)
    fun addTransaction(transaction: Transaction): Long {
        return database.insertTransaction(transaction)
    }
    fun getAllTransactions(): List<Transaction> {
        return database.getAllTransactions()
    }
    fun getBalance(): Pair<Double, Double> {
        val transactions = getAllTransactions()
        val credits = transactions
            .filter { it.type == TransactionType.CREDIT }
            .sumOf { it.amount }
        val debits = transactions
            .filter { it.type == TransactionType.DEBIT }
            .sumOf { it.amount }
        return Pair(credits, debits)
    }

    fun getNetBalance(): Double {
        val (credits, debits) = getBalance()
        return credits - debits
    }

    fun addCreditTransaction(
        amount: Double,
        description: String,
        creditDetail: CreditDetail
    ): Long {
        val transaction = Transaction(
            amount = amount,
            description = description,
            type = TransactionType.CREDIT,
            creditDetail = creditDetail,
            debitDetail = null
        )
        return addTransaction(transaction)
    }

    fun addDebitTransaction(
        amount: Double,
        description: String,
        debitDetail: DebitDetail
    ): Long {
        val transaction = Transaction(
            amount = amount,
            description = description,
            type = TransactionType.DEBIT,
            creditDetail = null,
            debitDetail = debitDetail
        )
        return addTransaction(transaction)
    }
}