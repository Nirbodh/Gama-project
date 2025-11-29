package com.gama.gama.models

data class UserResponse(
    val _id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val walletBalance: Double,
    val token: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

data class LoginRequest(
    val email: String,
    val password: String
)