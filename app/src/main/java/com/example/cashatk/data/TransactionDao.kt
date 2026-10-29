package com.example.cashatk.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: ExpenseTransaction)

    @Update
    suspend fun updateTransaction(transaction: ExpenseTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: ExpenseTransaction)

    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    suspend fun getAllTransactions(): List<ExpenseTransaction>

    @Query("SELECT * FROM transactions WHERE categoryId = :catId ORDER BY dateTime DESC")
    suspend fun getTransactionsForCategory(catId: Int): List<ExpenseTransaction>
}