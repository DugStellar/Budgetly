package com.orizon.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvTotalBalance = findViewById<TextView>(R.id.tvTotalBalance)
        val rvExpenses = findViewById<RecyclerView>(R.id.rvExpenses)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val database = AppDatabase.getDatabase(this)

        adapter = ExpenseAdapter(emptyList())
        rvExpenses.layoutManager = LinearLayoutManager(this)
        rvExpenses.adapter = adapter

        // Observe Total
        database.expenseDao().getTotalExpenses().observe(this) { total ->
            tvTotalBalance.text = "R${String.format("%.2f", total ?: 0.0)}"
        }

        // Observe List
        database.expenseDao().getAllExpenses().observe(this) { adapter.updateData(it) }

        // Swipe-to-Delete
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val expense = adapter.getExpenseAt(viewHolder.adapterPosition)
                lifecycleScope.launch {
                    database.expenseDao().delete(expense)
                    Toast.makeText(this@DashboardActivity, "Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }).attachToRecyclerView(rvExpenses)

        fabAdd.setOnClickListener { startActivity(Intent(this, AddExpenseActivity::class.java)) }
    }
}