package com.orizon.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefs = getSharedPreferences("BudgetlyPrefs", MODE_PRIVATE)
        if (prefs.contains("CURRENT_USER")) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)
        val rvMainExpenses = findViewById<RecyclerView>(R.id.rvMainExpenses)
        val btnStart = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnStart)

        rvMainExpenses.layoutManager = LinearLayoutManager(this)
        expenseAdapter = ExpenseAdapter { expense ->
            lifecycleScope.launch {
                database.expenseDao().delete(expense)
                Toast.makeText(this@MainActivity, "Deleted", Toast.LENGTH_SHORT).show()
            }
        }
        rvMainExpenses.adapter = expenseAdapter

        // Load some public or sample expenses for the welcome screen
        database.expenseDao().getAllExpenses().observe(this) { expenses ->
            if (expenses.isNotEmpty()) {
                rvMainExpenses.visibility = View.VISIBLE
                expenseAdapter.submitList(expenses)
            } else {
                rvMainExpenses.visibility = View.GONE
            }
        }

        btnStart.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}