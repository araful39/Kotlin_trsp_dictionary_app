package com.example.kotlin_trsp_dictionary_app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kotlin_trsp_dictionary_app.R
import com.example.kotlin_trsp_dictionary_app.data.model.BottomTab
import com.example.kotlin_trsp_dictionary_app.ui.auth.LoginScreen
import com.example.kotlin_trsp_dictionary_app.ui.auth.RegisterScreen
import com.example.kotlin_trsp_dictionary_app.ui.home.HomeScreen
import com.example.kotlin_trsp_dictionary_app.ui.splash.SplashScreen
import com.example.kotlin_trsp_dictionary_app.ui.tabs.LearnScreen
import com.example.kotlin_trsp_dictionary_app.ui.tabs.ProfileScreen
import com.example.kotlin_trsp_dictionary_app.ui.tabs.SavedScreen
import com.example.kotlin_trsp_dictionary_app.ui.tabs.SubjectsScreen

// 5. JETPACK NAVIGATION - 5 ta tab er jonno.
// NavController = kon screen e achi seta mone rakhe.

// Root routes: splash -> login <-> register -> main (5 tabs)
object RootRoute {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

// 0. ROOT NAV - app start ekhan theke.
@Composable
fun AppRootNav() {
    val rootNav = rememberNavController()
    NavHost(
        navController = rootNav,
        startDestination = RootRoute.SPLASH
    ) {
        composable(RootRoute.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    rootNav.navigate(RootRoute.LOGIN) {
                        popUpTo(RootRoute.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(RootRoute.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    rootNav.navigate(RootRoute.MAIN) {
                        popUpTo(RootRoute.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    rootNav.navigate(RootRoute.REGISTER)
                }
            )
        }
        composable(RootRoute.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    rootNav.navigate(RootRoute.MAIN) {
                        popUpTo(RootRoute.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    rootNav.popBackStack()
                }
            )
        }
        composable(RootRoute.MAIN) {
            DictionaryApp()
        }
    }
}

@Composable
fun DictionaryApp() {
    // 1. NavController banao (mone rakhar khata)
    val navController = rememberNavController()

    // 2. Scaffold = niche bottom bar + majhe screen jayga
    Scaffold(
        bottomBar = { BottomBar(navController) },
        containerColor = Color.White
    ) { padding ->
        // 3. NavHost = kon route e kon screen
        NavHost(
            navController = navController,
            startDestination = BottomTab.HOME.route, // prothome Home
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomTab.HOME.route) { HomeScreen() }
            composable(BottomTab.SUBJECTS.route) { SubjectsScreen() }
            composable(BottomTab.SAVED.route) { SavedScreen() }
            composable(BottomTab.LEARN.route) { LearnScreen() }
            composable(BottomTab.PROFILE.route) { ProfileScreen() }
        }
    }
}

// Nicher 5 ta button
@Composable
fun BottomBar(nav: NavController) {
    // Ekhon kon route e achi?
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Column {
        HorizontalDivider(thickness = 1.dp, color = colorResource(R.color.trsp_search_stroke))
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 5 ta tab er jonno loop
            for (tab in BottomTab.entries) {
                val selected = (tab.route == currentRoute)
                val tint = if (selected) colorResource(R.color.trsp_red) else Color.Gray

                Column(
                    modifier = Modifier.clickable {
                        // Tab press -> oi route e jao
                        nav.navigate(tab.route) {
                            popUpTo(BottomTab.HOME.route) // back stack poriskar
                            launchSingleTop = true // ek e screen 2 bar khulbe na
                        }
                    }.padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(tab.iconRes),
                        contentDescription = tab.label,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(tint)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(tab.label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = tint)
                }
            }
        }
    }
}
