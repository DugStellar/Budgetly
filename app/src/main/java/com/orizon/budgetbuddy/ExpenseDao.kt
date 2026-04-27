package com.orizon.budgetbuddy

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense_table ORDER BY timestamp DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT SUM(amount) FROM expense_table")
    fun getTotalExpenses(): LiveData<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)
}