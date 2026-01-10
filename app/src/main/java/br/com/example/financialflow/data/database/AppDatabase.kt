package br.com.example.financialflow.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppDatabase(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "fluxo_caixa.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_TRANSACTIONS = "transactions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CREATED_AT = "created_at"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDatabase(context).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_CREATED_AT TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
            onCreate(db)
        }
    }

    fun insertTransaction(transaction: Transaction): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_TYPE, transaction.type.name)
            put(COLUMN_DATE, transaction.date)
            put(COLUMN_CREATED_AT, transaction.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        }
        return db.insert(TABLE_TRANSACTIONS, null, values)
    }

    fun getAllTransactions(): List<Transaction> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null, null, null, null, null,
            "$COLUMN_CREATED_AT DESC"
        )

        val transactions = mutableListOf<Transaction>()
        while (cursor.moveToNext()) {
            transactions.add(cursorToTransaction(cursor))
        }
        cursor.close()
        return transactions
    }

    fun getBalance(): Pair<Double, Double> {
        val db = readableDatabase
        var credits = 0.0
        var debits = 0.0

        val creditQuery = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COLUMN_TYPE = '${TransactionType.CREDIT.name}'"
        db.rawQuery(creditQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                credits = cursor.getDouble(0)
            }
        }

        val debitQuery = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COLUMN_TYPE = '${TransactionType.DEBIT.name}'"
        db.rawQuery(debitQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                debits = cursor.getDouble(0)
            }
        }

        return Pair(credits, debits)
    }

    private fun cursorToTransaction(cursor: Cursor): Transaction {
        return Transaction(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: "",
            type = TransactionType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))),
            date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
            createdAt = LocalDateTime.parse(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            )
        )
    }
}