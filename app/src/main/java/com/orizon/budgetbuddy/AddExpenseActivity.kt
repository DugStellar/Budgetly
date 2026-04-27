package com.orizon.budgetbuddy

import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var ivPreview: ImageView
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) ivPreview.setImageBitmap(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val etDesc = findViewById<EditText>(R.id.etDescription)
        val etAmt = findViewById<EditText>(R.id.etAmount)
        ivPreview = findViewById(R.id.ivReceiptPreview)

        findViewById<Button>(R.id.btnCapture).setOnClickListener { takePicture.launch(null) }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val amt = etAmt.text.toString().toDoubleOrNull()
            if (etDesc.text.isNotEmpty() && amt != null) {
                lifecycleScope.launch {
                    AppDatabase.getDatabase(this@AddExpenseActivity).expenseDao().insert(
                        Expense(description = etDesc.text.toString(), amount = amt, category = "General")
                    )
                    finish()
                }
            }
        }
    }
}