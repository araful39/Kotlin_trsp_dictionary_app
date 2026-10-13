package com.example.kotlin_trsp_dictionary_app.data.model

import com.example.kotlin_trsp_dictionary_app.R

// 1. MODEL (M in MVVM) - only data, no logic.

// One category box (Physics, Chemistry...)
data class Category(
    val id: String,
    val title: String,
    val emoji: String // easy icon for beginner, no vector needed
)

// Term of the day card data
data class TermOfDay(
    val title: String = "Entropy",
    val description: String = "A measure related to the number of possible arrangements of a system."
)

// Bottom bar tabs. route = navigation path.
enum class BottomTab(val route: String, val label: String, val iconRes: Int) {
    HOME("home", "Home", R.drawable.ic_nav_home),
    SUBJECTS("subjects", "Subjects", R.drawable.ic_nav_subjects),
    SAVED("saved", "Saved", R.drawable.ic_nav_saved),
    LEARN("learn", "Learn", R.drawable.ic_nav_learn),
    PROFILE("profile", "Profile", R.drawable.ic_nav_profile)
}
