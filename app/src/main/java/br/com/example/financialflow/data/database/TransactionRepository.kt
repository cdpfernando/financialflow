package br.com.example.financialflow.data.database

import android.content.Context
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import java.time.LocalDateTime

class TransactionRepository(private val database: AppDatabase) {

    constructor(context: Context) : this(AppDatabase.getInstance(context))

    fun addTransaction(transaction: Transaction): Long {
        return database.insertTransaction(transaction)
    }

    fun getAllTransactions(): List<Transaction> {
        return database.getAllTransactions()
    }

    fun getBalance(): Pair<Double, Double> {
        return database.getBalance()
    }

    fun getNetBalance(): Double {
        val (credits, debits) = getBalance()
        return credits - debits
    }

    fun addTransaction(
        amount: Double,
        description: String,
        date: String,
        type: TransactionType,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): Long {
        val transaction = Transaction(
            amount = amount,
            description = description,
            type = type,
            date = date,
            createdAt = createdAt
        )
        return addTransaction(transaction)
    }
}