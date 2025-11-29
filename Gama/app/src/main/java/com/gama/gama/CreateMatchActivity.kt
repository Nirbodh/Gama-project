package com.gama.gama

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gama.gama.models.CreateMatchRequest
import com.gama.gama.network.ApiClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateMatchActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etGameType: EditText
    private lateinit var etEntryFee: EditText
    private lateinit var etPrizePool: EditText
    private lateinit var etMaxParticipants: EditText
    private lateinit var btnCreate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_match)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etGameType = findViewById(R.id.etGameType)
        etEntryFee = findViewById(R.id.etEntryFee)
        etPrizePool = findViewById(R.id.etPrizePool)
        etMaxParticipants = findViewById(R.id.etMaxParticipants)
        btnCreate = findViewById(R.id.btnCreate)
    }

    private fun setupClickListeners() {
        btnCreate.setOnClickListener {
            createMatch()
        }
    }

    private fun createMatch() {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val gameType = etGameType.text.toString().trim()
        val entryFeeText = etEntryFee.text.toString().trim()
        val prizePoolText = etPrizePool.text.toString().trim()
        val maxParticipantsText = etMaxParticipants.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || gameType.isEmpty() ||
            entryFeeText.isEmpty() || prizePoolText.isEmpty() || maxParticipantsText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val entryFee = entryFeeText.toDoubleOrNull()
        val prizePool = prizePoolText.toDoubleOrNull()
        val maxParticipants = maxParticipantsText.toIntOrNull()

        if (entryFee == null || prizePool == null || maxParticipants == null) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        // Set start time to 1 hour from now
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 1)
        val startTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(calendar.time)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.createMatch(
                    CreateMatchRequest(
                        title = title,
                        description = description,
                        type = "normal",
                        gameType = gameType,
                        entryFee = entryFee,
                        prizePool = prizePool,
                        maxParticipants = maxParticipants,
                        startTime = startTime,
                        rules = listOf("No cheating", "Fair play only")
                    )
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@CreateMatchActivity, "Match created! Waiting for admin approval.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateMatchActivity, "Failed to create match", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreateMatchActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}