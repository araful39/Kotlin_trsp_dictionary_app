package com.example.kotlin_trsp_dictionary_app.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.10.11.156:5000/"

    // Ekta-i Retrofit build, shob service or theke toiri hobe (connection reuse).
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // sheshe / lagbei lagbe
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val classApiService: ClassApiService by lazy { retrofit.create(ClassApiService::class.java) }
}
