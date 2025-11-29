package com.gama.gama.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.gama.gama.MainActivity
import com.gama.gama.R
import com.gama.gama.network.ApiClient
import com.gama.gama.utils.TokenManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "LoginActivity started")

        // Set content view first
        setContentView(R.layout.activity_login)

        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_color)

        tokenManager = TokenManager(this)
        initializeViews()
        setupClickListeners()

        // Auto-fill test credentials for easier testing
        etEmail.setText("test@example.com")
        etPassword.setText("123456")
    }

    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        Log.d(TAG, "All views initialized successfully")
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields")
            return
        }

        if (password.length < 6) {
            showToast("Password must be at least 6 characters")
            return
        }

        // Show loading state
        setLoadingState(true)

        lifecycleScope.launch {
            try {
                Log.d(TAG, "Attempting login for: $email")

                val response = ApiClient.apiService.login(
                    com.gama.gama.models.LoginRequest(email = email, password = password)
                )

                Log.d(TAG, "Login response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    Log.d(TAG, "Login successful for user: ${user.name}")

                    // Save token and user data
                    tokenManager.saveToken(user.token)
                    tokenManager.saveUserData(user._id, user.role)

                    // Set token for future API calls
                    ApiClient.setAuthToken(user.token)

                    showToast("Welcome ${user.name}!")

                    // Navigate to Main Activity - SIMPLE AND GUARANTEED
                    navigateToMain()

                } else {
                    Log.e(TAG, "Login failed with code: ${response.code()}")
                    when (response.code()) {
                        401 -> showToast("Invalid email or password")
                        400 -> showToast("User not found")
                        else -> showToast("Login failed: ${response.code()}")
                    }
                    setLoadingState(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network error: ${e.message}", e)
                showToast("Network error: Check your connection")
                setLoadingState(false)
            }
        }
    }

    private fun navigateToMain() {
        try {
            Log.d(TAG, "Navigating to MainActivity")
            val intent = Intent(this@LoginActivity, MainActivity::class.java)

            // Clear back stack and start fresh
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()

        } catch (e: Exception) {
            Log.e(TAG, "Navigation error: ${e.message}", e)
            showToast("Navigation error")
            setLoadingState(false)
        }
    }

    private fun setLoadingState(loading: Boolean) {
        btnLogin.isEnabled = !loading
        btnLogin.text = if (loading) "Logging in..." else "Login"
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "LoginActivity resumed")
        // Reset loading state when returning to login
        setLoadingState(false)
    }
}