package com.example.kotlin_trsp_dictionary_app.data.repository

import com.example.kotlin_trsp_dictionary_app.data.model.LoginRequest
import com.example.kotlin_trsp_dictionary_app.data.model.LoginResponse
import com.example.kotlin_trsp_dictionary_app.data.model.RegisterRequest
import com.example.kotlin_trsp_dictionary_app.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    private val api = RetrofitClient.authApiService

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(identifier = email, password = password))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Login failed (${response.code()})"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Register - login er ek i pattern: IO thread e call, Result<> te ferot.
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        classId: String
    ): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password,
                    classId = classId
                )
                val response = api.register(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Registration failed (${response.code()})"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
