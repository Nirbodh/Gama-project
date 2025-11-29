package com.gama.gama.network

import com.gama.gama.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<UserResponse>

    @GET("auth/profile")
    suspend fun getProfile(): Response<UserResponse>

    @GET("matches")
    suspend fun getMatches(@Query("status") status: String? = null): Response<List<Match>>

    @POST("matches")
    suspend fun createMatch(@Body request: CreateMatchRequest): Response<Match>

    @POST("matches/{id}/join")
    suspend fun joinMatch(@Path("id") matchId: String): Response<JoinMatchResponse>

    @POST("matches/{id}/submit-result")
    suspend fun submitResult(@Path("id") matchId: String, @Body request: SubmitResultRequest): Response<Match>

    @POST("wallet/deposit")
    suspend fun requestDeposit(@Body request: DepositRequest): Response<Deposit>

    @POST("wallet/withdraw")
    suspend fun requestWithdrawal(@Body request: WithdrawalRequest): Response<Withdrawal>

    @GET("wallet/transactions")
    suspend fun getTransactions(): Response<TransactionResponse>

    @GET("prizes/user")
    suspend fun getUserPrizes(): Response<List<Prize>>
}