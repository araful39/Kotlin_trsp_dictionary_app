package com.example.kotlin_trsp_dictionary_app.di

import com.example.kotlin_trsp_dictionary_app.data.repository.AuthRepository
import com.example.kotlin_trsp_dictionary_app.data.repository.ClassRepository
import com.example.kotlin_trsp_dictionary_app.data.repository.HomeRepository

// 2.5 DI (dependency injection) - beginner level:
// Repository gulo ekhane (ek bar) banao. Porer sob jaygay ei same copy use hobe.
// Kono ViewModel er moddhe "AuthRepository()" ar likhte hobe na.
object AppContainer {
    val authRepository: AuthRepository by lazy { AuthRepository() }
    val homeRepository: HomeRepository by lazy { HomeRepository() }
    val classRepository: ClassRepository by lazy { ClassRepository() }
}
