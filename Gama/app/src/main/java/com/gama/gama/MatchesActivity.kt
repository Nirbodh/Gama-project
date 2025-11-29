package com.gama.gama

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gama.gama.models.Match
import com.gama.gama.network.ApiClient
import kotlinx.coroutines.launch

class MatchesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoMatches: TextView
    private lateinit var btnCreateMatch: Button
    private var matchesList = mutableListOf<Match>()
    private lateinit var adapter: MatchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches)

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadMatches()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.rvMatches)
        tvNoMatches = findViewById(R.id.tvNoMatches)
        btnCreateMatch = findViewById(R.id.btnCreateMatch)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MatchAdapter(matchesList,
            onItemClick = { match ->
                showMatchDetails(match)
            },
            onJoinClick = { match ->
                joinMatch(match)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        btnCreateMatch.setOnClickListener {
            val intent = Intent(this, CreateMatchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadMatches() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getMatches("approved")
                if (response.isSuccessful && response.body() != null) {
                    matchesList.clear()
                    matchesList.addAll(response.body()!!)
                    adapter.updateMatches(matchesList)

                    // Show/hide no matches message
                    if (matchesList.isEmpty()) {
                        tvNoMatches.visibility = TextView.VISIBLE
                        recyclerView.visibility = RecyclerView.GONE
                    } else {
                        tvNoMatches.visibility = TextView.GONE
                        recyclerView.visibility = RecyclerView.VISIBLE
                    }
                } else {
                    Toast.makeText(this@MatchesActivity, "Failed to load matches", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MatchesActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showMatchDetails(match: Match) {
        // For now, just show a toast. We can create a detailed match activity later
        Toast.makeText(this,
            "${match.title}\nGame: ${match.gameType}\nEntry: ৳${match.entryFee}\nPrize: ৳${match.prizePool}",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun joinMatch(match: Match) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.joinMatch(match._id)
                if (response.isSuccessful) {
                    Toast.makeText(this@MatchesActivity, "Successfully joined the match!", Toast.LENGTH_SHORT).show()
                    loadMatches() // Reload to update participant count
                } else {
                    when (response.code()) {
                        400 -> Toast.makeText(this@MatchesActivity, "Already joined or insufficient balance", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@MatchesActivity, "Failed to join match", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@MatchesActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh matches when returning from CreateMatchActivity
        loadMatches()
    }
}