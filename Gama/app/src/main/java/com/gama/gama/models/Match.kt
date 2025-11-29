package com.gama.gama.models

data class Match(
    val _id: String,
    val title: String,
    val description: String,
    val type: String,
    val gameType: String,
    val entryFee: Double,
    val prizePool: Double,
    val maxParticipants: Int,
    val currentParticipants: Int,
    val status: String,
    val createdBy: User?,
    val participants: List<Participant>,
    val startTime: String,
    val winner: String?,
    val screenshot: String?,
    val rules: List<String>,
    val createdAt: String
)

data class User(
    val _id: String,
    val name: String,
    val email: String
)

data class CreateMatchRequest(
    val title: String,
    val description: String,
    val type: String,
    val gameType: String,
    val entryFee: Double,
    val prizePool: Double,
    val maxParticipants: Int,
    val startTime: String,
    val rules: List<String>
)

data class Participant(
    val user: String,
    val joinedAt: String
)

data class JoinMatchResponse(
    val message: String,
    val match: Match
)

data class SubmitResultRequest(
    val screenshot: String
)