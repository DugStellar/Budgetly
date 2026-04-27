package com.orizon.budgetbuddy

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense_table ORDER BY timestamp DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    // The new Total Balance query
    @Query("SELECT SUM(amount) FROM expense_table")
    fun getTotalExpenses(): LiveData<Double?>

    @Delete
    suspend fun delete(expense: Expense)
}