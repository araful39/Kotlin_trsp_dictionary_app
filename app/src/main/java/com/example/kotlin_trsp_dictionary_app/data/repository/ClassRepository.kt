package com.example.kotlin_trsp_dictionary_app.data.repository

import com.example.kotlin_trsp_dictionary_app.data.model.ClassOption
import com.example.kotlin_trsp_dictionary_app.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Class list er data supply (GET api/classes).
// Response theke shudhu dropdown er dorkari jinis ber kore: slug -> value, name -> label.
class ClassRepository {

    private val api = RetrofitClient.classApiService

    suspend fun getClasses(): Result<List<ClassOption>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getClasses()
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success == false) {
                        // Server "success: false" pathaise (message shoho)
                        Result.failure(Exception(body.message ?: "Failed to load classes"))
                    } else {
                        val options = (body.data ?: emptyList())
                            .filter { it.isPublished != false }          // shudhu published class
                            .sortedBy { it.order ?: Int.MAX_VALUE }      // order onujayi (1,2,3...)
                            .mapNotNull { item ->
                                val slug = item.slug
                                val name = item.name
                                if (slug != null && name != null) ClassOption(value = slug, label = name) else null
                            }
                        Result.success(options)
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Classes failed (${response.code()})"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e) // internet nai / server bondho
            }
        }
    }
}
