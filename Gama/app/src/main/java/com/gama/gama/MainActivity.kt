package com.gama.gama

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gama.gama.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "🚀 MainActivity started")

        // Set the layout FIRST - this is crucial
        setContentView(R.layout.activity_main)
        Log.d(TAG, "✅ Layout set successfully")

        // Show immediate feedback
        Toast.makeText(this, "Welcome to Gaming Tournament!", Toast.LENGTH_SHORT).show()

        setupNavigation()
    }

    private fun setupNavigation() {
        try {
            Log.d(TAG, "Setting up navigation buttons")

            val btnMatches = findViewById<Button>(R.id.btnMatches)
            val btnWallet = findViewById<Button>(R.id.btnWallet)
            val btnProfile = findViewById<Button>(R.id.btnProfile)

            btnMatches.setOnClickListener {
                Log.d(TAG, "Matches button clicked")
                startActivity(Intent(this, MatchesActivity::class.java))
            }

            btnWallet.setOnClickListener {
                Log.d(TAG, "Wallet button clicked")
                startActivity(Intent(this, WalletActivity::class.java))
            }

            btnProfile.setOnClickListener {
                Log.d(TAG, "Profile button clicked")
                startActivity(Intent(this, ProfileActivity::class.java))
            }

            Log.d(TAG, "✅ Navigation setup completed")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error in navigation setup: ${e.message}", e)
            Toast.makeText(this, "Navigation error", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity resumed")
    }
}