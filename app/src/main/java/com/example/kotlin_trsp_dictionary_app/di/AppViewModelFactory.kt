package com.example.kotlin_trsp_dictionary_app.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kotlin_trsp_dictionary_app.ui.auth.LoginViewModel
import com.example.kotlin_trsp_dictionary_app.ui.auth.RegisterViewModel
import com.example.kotlin_trsp_dictionary_app.ui.home.HomeViewModel

// 2.6 ViewModel Factory - todate: kon ViewModel e kon repository lagbe seta boro kore lekha.
// Screen e emni use hoy:
//     vm: LoginViewModel = viewModel(factory = AppViewModelFactory)
// line by line: factory banao -> LoginViewModel(repo) -> screen e chole jay.
val AppViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
    initializer { LoginViewModel(AppContainer.authRepository) }  // login e repository lagbe
    initializer { RegisterViewModel(AppContainer.authRepository, AppContainer.classRepository) } // register + class list
    initializer { HomeViewModel(AppContainer.homeRepository) }  // home e repository lagbe
}
