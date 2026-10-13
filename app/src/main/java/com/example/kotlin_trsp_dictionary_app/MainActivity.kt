package com.example.kotlin_trsp_dictionary_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kotlin_trsp_dictionary_app.ui.navigation.AppRootNav
import com.example.kotlin_trsp_dictionary_app.ui.theme.Kotlin_trsp_dictionary_appTheme

// App start ekhan theke. Jetpack Compose UI.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kotlin_trsp_dictionary_appTheme(dynamicColor = false) {
                AppRootNav() // Splash -> Login -> Main (5 tab)
            }
        }
    }
}
