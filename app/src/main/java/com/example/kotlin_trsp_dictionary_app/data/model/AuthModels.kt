package com.example.kotlin_trsp_dictionary_app.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("identifier")
    val identifier: String,
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    val success: Boolean? = null,
    val token: String? = null,
    val message: String? = null,
    val user: UserData? = null,
    val error: String? = null
)

data class UserData(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null
)

// POST api/auth/register - je JSON pathabo seta.
data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val classId: String
)

// Class dropdown er option. value = API te jay (slug), label = UI te dekhay (name).
data class ClassOption(val value: String, val label: String)

// FALLBACK list: API call fail hole (internet nai etc.) ei diye dropdown cholbe.
// API success hole ei list ke diye replace hoy. Values = asol server er slug.
val CLASS_OPTIONS = listOf(
    ClassOption("class-6-8", "Class 6-8"),
    ClassOption("class-9-10", "Class 9-10"),
    ClassOption("hsc", "HSC (Class 11-12)"),
    ClassOption("undergraduate", "Undergraduate")
)
