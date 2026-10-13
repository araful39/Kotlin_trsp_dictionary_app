package com.example.kotlin_trsp_dictionary_app.data.repository

import com.example.kotlin_trsp_dictionary_app.data.model.Category
import com.example.kotlin_trsp_dictionary_app.data.model.TermOfDay

// 2. REPOSITORY - data dewar kaj. Ekhon static list, pore Room/API bosano jabe.
class HomeRepository {

    // 6 ta category
    fun getCategories(): List<Category> {
        return listOf(
            Category("physics", "Physics", "🚀"),
            Category("chemistry", "Chemistry", "⚗️"),
            Category("biology", "Biology", "🌿"),
            Category("astronomy", "Astronomy", "🌙"),
            Category("math", "Math", "📐"),
            Category("compsci", "Comp. Sci", "💻")
        )
    }



    // Recent search list
    fun getRecentSearches(): List<String> {
        return listOf("Isotope", "Photosynthesis", "Entropy", "Velocity")
    }

    // Term of the day
    fun getTermOfDay(): TermOfDay {
        return TermOfDay()
    }
}
