package com.example.kotlin_trsp_dictionary_app.data.network

import com.example.kotlin_trsp_dictionary_app.data.model.LoginRequest
import com.example.kotlin_trsp_dictionary_app.data.model.LoginResponse
import com.example.kotlin_trsp_dictionary_app.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // Register: same shape response as login (success/token/message/error).
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>
}
