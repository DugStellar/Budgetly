package com.orizon.budgetbuddy

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var ivReceiptPreview: ImageView
    private var capturedImage: Bitmap? = null

    // This handles the result coming back from the camera
    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            capturedImage = bitmap
            ivReceiptPreview.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnCapture = findViewById<Button>(R.id.btnCapture)
        val btnSave = findViewById<Button>(R.id.btnSave)
        ivReceiptPreview = findViewById(R.id.ivReceiptPreview)

        // Launch Camera
        btnCapture.setOnClickListener {
            takePicturePreview.launch(null)
        }

        // Save to Database
        btnSave.setOnClickListener {
            val desc = etDescription.text.toString()
            val amt = etAmount.text.toString().toDoubleOrNull()

            if (desc.isNotEmpty() && amt != null) {
                saveExpense(desc, amt)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveExpense(description: String, amount: Double) {
        val database = AppDatabase.getDatabase(this)
        val newExpense = Expense(
            description = description,
            amount = amount,
            category = "General" // You can add a spinner to select categories later
        )

        lifecycleScope.launch {
            database.expenseDao().insert(newExpense)
            Toast.makeText(this@AddExpenseActivity, "Expense Added!", Toast.LENGTH_SHORT).show()
            finish() // Goes back to Dashboard
        }
    }
}