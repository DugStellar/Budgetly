package com.orizon.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvTotalBalance = findViewById<TextView>(R.id.tvTotalBalance)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        // Initialize Database
        val database = AppDatabase.getDatabase(this)

        // Observe the Total Expenses
        database.expenseDao().getTotalExpenses().observe(this) { total ->
            val displayTotal = total ?: 0.0
            // Formats to 2 decimal places (e.g., R150.50)
            tvTotalBalance.text = "R${String.format("%.2f", displayTotal)}"
        }

        // Floating Action Button to go to Add Expense screen
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }
}