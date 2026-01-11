package br.com.example.financialflow.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import br.com.example.financialflow.data.database.AppDatabase.Companion.COLUMN_AMOUNT
import br.com.example.financialflow.data.database.AppDatabase.Companion.COLUMN_CREATED_AT
import br.com.example.financialflow.data.database.AppDatabase.Companion.COLUMN_DATE
import br.com.example.financialflow.data.database.AppDatabase.Companion.COLUMN_DESCRIPTION
import br.com.example.financialflow.data.database.AppDatabase.Companion.COLUMN_ID
import br.com.example.financialflow.data.database.AppDatabase.Companion.COLUMN_TYPE
import br.com.example.financialflow.data.database.AppDatabase.Companion.TABLE_TRANSACTIONS
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransactionLocalDatasource(private val database: AppDatabase) {
    constructor(context: Context) : this(AppDatabase.getInstance(context))

    fun insertTransaction(transaction: Transaction): Long {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_TYPE, transaction.type.name)
            put(COLUMN_DATE, transaction.date)
            put(
                COLUMN_CREATED_AT,
                transaction.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }
        return db.insert(TABLE_TRANSACTIONS, null, values)
    }

    fun getAllTransactions(): List<Transaction> {
        val db = database.readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null, null, null, null, null,
            "$COLUMN_CREATED_AT DESC"
        )

        val transactions = mutableListOf<Transaction>()
        while (cursor.moveToNext()) {
            transactions.add(cursor.toTransaction())
        }
        cursor.close()
        return transactions
    }

    fun getBalance(): Pair<Double, Double> {
        val db = database.readableDatabase
        var credits = 0.0
        var debits = 0.0

        val creditQuery =
            "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COLUMN_TYPE = '${TransactionType.CREDIT.name}'"
        db.rawQuery(creditQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                credits = cursor.getDouble(0)
            }
        }

        val debitQuery =
            "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COLUMN_TYPE = '${TransactionType.DEBIT.name}'"
        db.rawQuery(debitQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                debits = cursor.getDouble(0)
            }
        }

        return Pair(credits, debits)
    }

}

private fun Cursor.toTransaction(): Transaction {
    return Transaction(
        id = getLong(getColumnIndexOrThrow(COLUMN_ID)),
        amount = getDouble(getColumnIndexOrThrow(COLUMN_AMOUNT)),
        description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: "",
        type = TransactionType.valueOf(getString(getColumnIndexOrThrow(COLUMN_TYPE))),
        date = getString(getColumnIndexOrThrow(COLUMN_DATE)),
        createdAt = LocalDateTime.parse(
            getString(getColumnIndexOrThrow(COLUMN_CREATED_AT)),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
    )
}