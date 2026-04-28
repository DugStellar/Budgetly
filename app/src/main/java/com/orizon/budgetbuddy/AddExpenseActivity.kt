package com.orizon.budgetbuddy

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.orizon.budgetbuddy.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var database: AppDatabase
    private val calendar = Calendar.getInstance()
    private var photoUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
            binding.tvPhotoStatus.text = "Photo attached"
            
            // Take persistent permission for the URI
            try {
                val contentResolver = contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        setupCategories()
        setupDatePicker()
        setupTimePickers()

        binding.btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        binding.btnAddPhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSaveExpense.setOnClickListener {
            val amountStr = binding.etAmount.text.toString()
            val category = binding.spnCategory.selectedItem?.toString() ?: "Other"
            val note = binding.etNote.text.toString()
            val date = binding.etDate.text.toString() // yyyy-MM-dd
            val startTime = binding.etStartTime.text.toString()
            val endTime = binding.etEndTime.text.toString()

            if (amountStr.isNotEmpty()) {
                val amount = amountStr.toDoubleOrNull() ?: 0.0
                saveExpense(amount, category, note, date, startTime, endTime)
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCategories() {
        database.categoryDao().getAllCategories().observe(this, Observer { categories ->
            val categoryNames = categories.map { it.name }.toMutableList()
            if (categoryNames.isEmpty()) {
                // Add default categories if none exist
                lifecycleScope.launch {
                    val defaults = listOf("Groceries", "Transport", "Rent", "Utilities", "Entertainment", "Other")
                    defaults.forEach { database.categoryDao().insert(Category(name = it)) }
                }
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnCategory.adapter = adapter
        })
    }

    private fun showAddCategoryDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Add New Category")
        val input = android.widget.EditText(this)
        input.hint = "Category Name"
        builder.setView(input)
        builder.setPositiveButton("Add") { _, _ ->
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                lifecycleScope.launch {
                    val existing = database.categoryDao().getCategoryByName(name)
                    if (existing == null) {
                        database.categoryDao().insert(Category(name = name))
                    } else {
                        Toast.makeText(this@AddExpenseActivity, "Category exists", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        builder.setNegativeButton("Cancel") { d, _ -> d.cancel() }
        builder.show()
    }

    private fun setupDatePicker() {
        updateLabel()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }
        binding.etDate.setOnClickListener {
            DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), 
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupTimePickers() {
        binding.etStartTime.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, h, m ->
                binding.etStartTime.setText(String.format("%02d:%02d", h, m))
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }
        binding.etEndTime.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, h, m ->
                binding.etEndTime.setText(String.format("%02d:%02d", h, m))
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }
    }

    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(calendar.time))
    }

    private fun saveExpense(amount: Double, category: String, note: String, date: String, start: String, end: String) {
        val expense = Expense(
            description = if (note.isEmpty()) category else note,
            amount = amount,
            category = category,
            date = date,
            startTime = start,
            endTime = end,
            photoUri = photoUri?.toString()
        )

        lifecycleScope.launch {
            database.expenseDao().insert(expense)
            Toast.makeText(this@AddExpenseActivity, "Expense Added!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}