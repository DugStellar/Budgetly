package com.orizon.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DashboardActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var expenseAdapter: ExpenseAdapter
    private var minGoal: Double = 0.0
    private var maxGoal: Double = 0.0
    private var totalExpenses: Double = 0.0
    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        database = AppDatabase.getDatabase(this)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvTotalBalance = findViewById<TextView>(R.id.tvTotalBalance)
        val tvGoals = findViewById<TextView>(R.id.tvGoals)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val rvExpenses = findViewById<RecyclerView>(R.id.rvExpenses)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val btnLogout = findViewById<android.widget.ImageButton>(R.id.btnLogout)
        val btnAnalytics = findViewById<android.widget.ImageButton>(R.id.btnAnalytics)
        val btnSetGoals = findViewById<android.widget.Button>(R.id.btnSetGoals)
        val btnFilterDate = findViewById<android.widget.Button>(R.id.btnFilterDate)

        val prefs = getSharedPreferences("BudgetlyPrefs", MODE_PRIVATE)
        val username = prefs.getString("CURRENT_USER", "User") ?: "User"
        tvWelcome.text = "Welcome, $username"

        rvExpenses.layoutManager = LinearLayoutManager(this)
        expenseAdapter = ExpenseAdapter { expense ->
            lifecycleScope.launch {
                database.expenseDao().delete(expense)
                Toast.makeText(this@DashboardActivity, "Expense deleted", Toast.LENGTH_SHORT).show()
            }
        }
        rvExpenses.adapter = expenseAdapter

        btnLogout.setOnClickListener {
            prefs.edit().remove("CURRENT_USER").apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnAnalytics.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            intent.putExtra("START_DATE", startDate)
            intent.putExtra("END_DATE", endDate)
            startActivity(intent)
        }

        btnSetGoals.setOnClickListener {
            showSetGoalsDialog(username, tvGoals, tvStatus)
        }

        btnFilterDate.setOnClickListener {
            showDateRangePicker(btnFilterDate)
        }

        // Fetch User Goals
        lifecycleScope.launch {
            val user = database.userDao().getUserByUsername(username)
            minGoal = user?.minMonthlyGoal ?: 0.0
            maxGoal = user?.maxMonthlyGoal ?: 0.0
            updateGoalsUI(tvGoals, tvStatus)
        }

        // Initial Load
        loadExpenses(tvTotalBalance, tvGoals, tvStatus)

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }

    private fun loadExpenses(tvTotal: TextView, tvGoals: TextView, tvStatus: TextView) {
        val observer = Observer<List<Expense>> { expenses ->
            expenseAdapter.submitList(expenses)
            totalExpenses = expenses.sumOf { it.amount }
            tvTotal.text = "R${String.format("%.2f", totalExpenses)}"
            updateGoalsUI(tvGoals, tvStatus)
        }

        if (startDate != null && endDate != null) {
            database.expenseDao().getExpensesInRange(startDate!!, endDate!!).observe(this, observer)
        } else {
            database.expenseDao().getAllExpenses().observe(this, observer)
        }
    }

    private fun showDateRangePicker(btn: android.widget.Button) {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Period")
            .build()

        picker.addOnPositiveButtonClickListener { range ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            startDate = sdf.format(Date(range.first))
            endDate = sdf.format(Date(range.second))
            
            btn.text = "$startDate to $endDate"
            
            // Reload with filters
            val tvTotalBalance = findViewById<TextView>(R.id.tvTotalBalance)
            val tvGoals = findViewById<TextView>(R.id.tvGoals)
            val tvStatus = findViewById<TextView>(R.id.tvStatus)
            loadExpenses(tvTotalBalance, tvGoals, tvStatus)
        }
        picker.show(supportFragmentManager, "RANGE_PICKER")
    }

    private fun showSetGoalsDialog(username: String, tvGoals: TextView, tvStatus: TextView) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Set Monthly Goals")
        
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_set_goals, null)
        val etMin = view.findViewById<EditText>(R.id.etMinGoal)
        val etMax = view.findViewById<EditText>(R.id.etMaxGoal)
        
        etMin.setText(minGoal.toString())
        etMax.setText(maxGoal.toString())
        
        builder.setView(view)
        builder.setPositiveButton("Set") { _, _ ->
            val min = etMin.text.toString().toDoubleOrNull() ?: 0.0
            val max = etMax.text.toString().toDoubleOrNull() ?: 0.0
            lifecycleScope.launch {
                database.userDao().updateGoals(username, min, max)
                minGoal = min
                maxGoal = max
                updateGoalsUI(tvGoals, tvStatus)
            }
        }
        builder.setNegativeButton("Cancel") { d, _ -> d.cancel() }
        builder.show()
    }

    private fun updateGoalsUI(tvGoals: TextView, tvStatus: TextView) {
        tvGoals.text = "Min: R${minGoal.toInt()} - Max: R${maxGoal.toInt()}"
        
        when {
            totalExpenses < minGoal -> {
                tvStatus.text = "Below Min Goal"
                tvStatus.setTextColor(android.graphics.Color.GRAY)
            }
            totalExpenses > maxGoal -> {
                tvStatus.text = "Over Max Goal!"
                tvStatus.setTextColor(android.graphics.Color.parseColor("#D93025"))
            }
            else -> {
                tvStatus.text = "On Track"
                tvStatus.setTextColor(android.graphics.Color.parseColor("#1E8E3E"))
            }
        }
    }
}