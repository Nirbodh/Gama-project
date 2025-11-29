package com.gama.gama

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gama.gama.network.ApiClient
import com.gama.gama.ui.auth.LoginActivity
import com.gama.gama.utils.TokenManager
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserPhone: TextView
    private lateinit var tvUserRole: TextView
    private lateinit var tvUserBalance: TextView
    private lateinit var btnLogout: Button
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tokenManager = TokenManager(this)
        initializeViews()
        loadUserProfile()
        setupClickListeners()
    }

    private fun initializeViews() {
        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvUserPhone = findViewById(R.id.tvUserPhone)
        tvUserRole = findViewById(R.id.tvUserRole)
        tvUserBalance = findViewById(R.id.tvUserBalance)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    tvUserName.text = "Name: ${user.name}"
                    tvUserEmail.text = "Email: ${user.email}"
                    tvUserPhone.text = "Phone: ${user.phone}"
                    tvUserRole.text = "Role: ${user.role}"
                    tvUserBalance.text = "Balance: ৳${user.walletBalance}"
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        lifecycleScope.launch {
            tokenManager.clearData()
            ApiClient.clearAuthToken()

            Toast.makeText(this@ProfileActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}