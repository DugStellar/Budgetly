package com.orizon.budgetbuddy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val description: String,

    val amount: Double,

    val category: String,

    // This stores the location of the receipt photo on the device
    // It is "String?" (nullable) because not every expense will have a photo
    val imagePath: String? = null,

    // Good for sorting your dashboard later
    val timestamp: Long = System.currentTimeMillis()
)