package com.example.kotlin_trsp_dictionary_app.data.network

import com.example.kotlin_trsp_dictionary_app.data.model.ClassesResponse
import retrofit2.Response
import retrofit2.http.GET

// Class list anar API (login er sathe noy, tai alada interface).
interface ClassApiService {

    @GET("api/classes")
    suspend fun getClasses(): Response<ClassesResponse>
}
