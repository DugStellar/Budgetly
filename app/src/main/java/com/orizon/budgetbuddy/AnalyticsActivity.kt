package com.orizon.budgetbuddy

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var expenseAdapter: ExpenseAdapter
    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val btnBack = findViewById<android.widget.ImageButton>(R.id.btnBack)
        val tvTitle = findViewById<TextView>(R.id.tvAnalyticsTitle)
        val rvExpenses = findViewById<RecyclerView>(R.id.rvAnalyticsExpenses)

        database = AppDatabase.getDatabase(this)

        expenseAdapter = ExpenseAdapter { expense ->
            lifecycleScope.launch {
                database.expenseDao().delete(expense)
                Toast.makeText(this@AnalyticsActivity, "Deleted", Toast.LENGTH_SHORT).show()
            }
        }
        rvExpenses.adapter = expenseAdapter
        rvExpenses.layoutManager = LinearLayoutManager(this)

        startDate = intent.getStringExtra("START_DATE")
        endDate = intent.getStringExtra("END_DATE")

        updateTitle(tvTitle)

        btnBack.setOnClickListener { finish() }

        tvTitle.setOnClickListener {
            showDateRangePicker(tvTitle, pieChart)
        }

        loadAnalytics(pieChart)
    }

    private fun loadAnalytics(pieChart: PieChart) {
        val observer = Observer<List<Expense>> { expenses ->
            // Update List
            expenseAdapter.submitList(expenses)

            // Update Chart
            if (expenses != null && expenses.isNotEmpty()) {
                val categoryMap = expenses.groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }

                val entries = ArrayList<PieEntry>()
                for ((category, total) in categoryMap) {
                    entries.add(PieEntry(total.toFloat(), category))
                }

                val dataSet = PieDataSet(entries, "")
                dataSet.colors = getModernColors()
                dataSet.valueTextColor = Color.BLACK
                dataSet.valueTextSize = 14f

                pieChart.data = PieData(dataSet)
                pieChart.centerText = "Spending"
                pieChart.setHoleColor(Color.TRANSPARENT)
                pieChart.setCenterTextColor(Color.BLACK)
                pieChart.legend.textColor = Color.BLACK
                pieChart.description.isEnabled = false
                pieChart.animateY(1000)
                pieChart.invalidate()
            } else {
                pieChart.clear()
            }
        }

        if (startDate != null && endDate != null) {
            database.expenseDao().getExpensesInRange(startDate!!, endDate!!).observe(this, observer)
        } else {
            database.expenseDao().getAllExpenses().observe(this, observer)
        }
    }

    private fun showDateRangePicker(tvTitle: TextView, pieChart: PieChart) {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Period")
            .build()

        picker.addOnPositiveButtonClickListener { range ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            startDate = sdf.format(Date(range.first))
            endDate = sdf.format(Date(range.second))

            updateTitle(tvTitle)
            loadAnalytics(pieChart)
        }
        picker.show(supportFragmentManager, "ANALYTICS_RANGE")
    }

    private fun updateTitle(tvTitle: TextView) {
        if (startDate != null && endDate != null) {
            tvTitle.text = "$startDate to $endDate"
        } else {
            tvTitle.text = "All Spending"
        }
    }

    private fun getModernColors(): List<Int> {
        return listOf(
            Color.parseColor("#4285F4"), // Blue
            Color.parseColor("#34A853"), // Green
            Color.parseColor("#FBBC04"), // Yellow
            Color.parseColor("#EA4335"), // Red
            Color.parseColor("#46BDC6"), // Cyan
            Color.parseColor("#9C27B0")  // Purple
        )
    }
}