package com.example.kotlin_trsp_dictionary_app.data.model

// GET api/classes er response. Shob field nullable + default - JSON missing hole crash hobe na.
data class ClassesResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val data: List<ClassItem>? = null
)

// Server er ekta class. Dropdown er jonno shudhu name + slug dorkar,
// baki (id, order, createdAt...) pore other kaj e lage.
data class ClassItem(
    val id: String? = null,
    val name: String? = null,
    val slug: String? = null,
    val order: Int? = null,
    val isPublished: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
