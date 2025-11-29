package com.gama.gama

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gama.gama.models.DepositRequest
import com.gama.gama.models.WithdrawalRequest
import com.gama.gama.network.ApiClient
import kotlinx.coroutines.launch

class WalletActivity : AppCompatActivity() {

    private lateinit var tvBalance: TextView
    private lateinit var etDepositAmount: EditText
    private lateinit var etDepositTransactionId: EditText
    private lateinit var btnDeposit: Button
    private lateinit var etWithdrawAmount: EditText
    private lateinit var etWithdrawTransactionId: EditText
    private lateinit var btnWithdraw: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        initializeViews()
        setupClickListeners()
        loadBalance()
    }

    private fun initializeViews() {
        tvBalance = findViewById(R.id.tvBalance)
        etDepositAmount = findViewById(R.id.etDepositAmount)
        etDepositTransactionId = findViewById(R.id.etDepositTransactionId)
        btnDeposit = findViewById(R.id.btnDeposit)
        etWithdrawAmount = findViewById(R.id.etWithdrawAmount)
        etWithdrawTransactionId = findViewById(R.id.etWithdrawTransactionId)
        btnWithdraw = findViewById(R.id.btnWithdraw)
    }

    private fun setupClickListeners() {
        btnDeposit.setOnClickListener {
            requestDeposit()
        }

        btnWithdraw.setOnClickListener {
            requestWithdrawal()
        }
    }

    private fun loadBalance() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    tvBalance.text = "Balance: ৳${user.walletBalance}"
                }
            } catch (e: Exception) {
                Toast.makeText(this@WalletActivity, "Failed to load balance", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestDeposit() {
        val amountText = etDepositAmount.text.toString()
        val transactionId = etDepositTransactionId.text.toString()

        if (amountText.isEmpty() || transactionId.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.requestDeposit(DepositRequest(amount, transactionId))
                if (response.isSuccessful) {
                    Toast.makeText(this@WalletActivity, "Deposit request submitted for approval", Toast.LENGTH_SHORT).show()
                    etDepositAmount.text.clear()
                    etDepositTransactionId.text.clear()
                    loadBalance() // Refresh balance
                } else {
                    Toast.makeText(this@WalletActivity, "Deposit request failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@WalletActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestWithdrawal() {
        val amountText = etWithdrawAmount.text.toString()
        val transactionId = etWithdrawTransactionId.text.toString()

        if (amountText.isEmpty() || transactionId.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.requestWithdrawal(WithdrawalRequest(amount, transactionId))
                if (response.isSuccessful) {
                    Toast.makeText(this@WalletActivity, "Withdrawal request submitted for approval", Toast.LENGTH_SHORT).show()
                    etWithdrawAmount.text.clear()
                    etWithdrawTransactionId.text.clear()
                    loadBalance() // Refresh balance
                } else {
                    Toast.makeText(this@WalletActivity, "Withdrawal request failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@WalletActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}