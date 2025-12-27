package br.com.example.financialflow.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.com.example.financialflow.data.model.CreditDetail
import br.com.example.financialflow.data.model.DebitDetail
import br.com.example.financialflow.data.model.Transaction
import br.com.example.financialflow.data.model.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun TransactionType.toStorageString(): String = this.name
fun String.toTransactionType(): TransactionType = TransactionType.valueOf(this)

fun CreditDetail.toStorageString(): String = this.name
fun String.toCreditDetail(): CreditDetail? = try {
    CreditDetail.valueOf(this)
} catch (e: Exception) {
    null
}

fun DebitDetail.toStorageString(): String = this.name
fun String.toDebitDetail(): DebitDetail? = try {
    DebitDetail.valueOf(this)
} catch (e: Exception) {
    null
}

class AppDatabase(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "fluxo_caixa.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TRANSACTIONS = "transactions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_CREDIT_DETAIL = "credit_detail"
        private const val COLUMN_DEBIT_DETAIL = "debit_detail"
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
                $COLUMN_CREDIT_DETAIL TEXT,
                $COLUMN_DEBIT_DETAIL TEXT,
                $COLUMN_CREATED_AT TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        onCreate(db)
    }

    fun insertTransaction(transaction: Transaction): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_TYPE, transaction.type.toStorageString())
            put(COLUMN_CREDIT_DETAIL, transaction.creditDetail?.toStorageString())
            put(COLUMN_DEBIT_DETAIL, transaction.debitDetail?.toStorageString())
            put(COLUMN_CREATED_AT, transaction.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        }
        return db.insert(TABLE_TRANSACTIONS, null, values)
    }

    fun getAllTransactions(): List<Transaction> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_CREATED_AT DESC"
        )

        val transactions = mutableListOf<Transaction>()
        while (cursor.moveToNext()) {
            transactions.add(cursorToTransaction(cursor))
        }
        cursor.close()
        return transactions
    }

    private fun cursorToTransaction(cursor: Cursor): Transaction {
        val typeString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
        val type = typeString.toTransactionType()

        val creditDetailString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREDIT_DETAIL))
        val debitDetailString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEBIT_DETAIL))

        return Transaction(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: "",
            type = type,
            creditDetail = creditDetailString?.toCreditDetail(),
            debitDetail = debitDetailString?.toDebitDetail(),
            createdAt = LocalDateTime.parse(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            )
        )
    }
}