package br.com.example.financialflow.data.database

import android.content.Context
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import java.time.LocalDateTime

class TransactionRepository(private val datasource: TransactionLocalDatasource) {

    constructor(context: Context) : this(TransactionLocalDatasource(context))

    fun addTransaction(transaction: Transaction): Long {
        return datasource.insertTransaction(transaction)
    }

    fun getAllTransactions(): List<Transaction> {
        return datasource.getAllTransactions()
    }

    fun getBalance(): Pair<Double, Double> {
        return datasource.getBalance()
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
