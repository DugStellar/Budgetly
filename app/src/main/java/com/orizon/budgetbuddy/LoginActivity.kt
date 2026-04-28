package com.orizon.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.orizon.budgetbuddy.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

        binding.btnLogin.setOnClickListener {
            val user = binding.etUsername.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val match = db.userDao().login(user, pass)
                if (match != null) {
                    // Save username for session
                    val prefs = getSharedPreferences("BudgetlyPrefs", MODE_PRIVATE)
                    prefs.edit().putString("CURRENT_USER", match.username).apply()

                    Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                    intent.putExtra("USERNAME", match.username)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvCreateAccount.setOnClickListener {
            val user = binding.etUsername.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (user.isNotEmpty() && pass.isNotEmpty()) {
                lifecycleScope.launch {
                    val existingUser = db.userDao().getUserByUsername(user)
                    if (existingUser == null) {
                        val result = db.userDao().registerUser(User(username = user, password = pass))
                        if (result != -1L) {
                            Toast.makeText(this@LoginActivity, "Account Created! You can now log in.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@LoginActivity, "Registration failed, try again", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Enter details to create account", Toast.LENGTH_SHORT).show()
            }
        }
    }
}