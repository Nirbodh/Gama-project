package com.gama.gama.models

data class Deposit(
    val _id: String,
    val user: String,
    val amount: Double,
    val transactionId: String,
    val status: String,
    val createdAt: String
)

data class Withdrawal(
    val _id: String,
    val user: String,
    val amount: Double,
    val transactionId: String,
    val status: String,
    val createdAt: String
)

data class DepositRequest(
    val amount: Double,
    val transactionId: String
)

data class WithdrawalRequest(
    val amount: Double,
    val transactionId: String
)

data class TransactionResponse(
    val deposits: List<Deposit>,
    val withdrawals: List<Withdrawal>
)

data class Prize(
    val _id: String,
    val match: Match,
    val amount: Double,
    val position: Int,
    val distributedAt: String
)