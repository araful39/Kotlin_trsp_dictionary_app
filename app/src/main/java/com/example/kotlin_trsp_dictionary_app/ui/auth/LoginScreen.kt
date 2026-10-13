package com.example.kotlin_trsp_dictionary_app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_trsp_dictionary_app.R
import com.example.kotlin_trsp_dictionary_app.di.AppViewModelFactory
import com.example.kotlin_trsp_dictionary_app.ui.components.AppTextField
import com.example.kotlin_trsp_dictionary_app.ui.components.PrimaryButton
import com.example.kotlin_trsp_dictionary_app.ui.theme.Kotlin_trsp_dictionary_appTheme

// 4. VIEW (V in MVVM): state collect kore, event ViewModel e pathay,
// effect (navigate) collect kore. Logic UI te nai.
// Login screen: email + password, valid hole home e jabe.
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    vm: LoginViewModel = viewModel(factory = AppViewModelFactory),
    modifier: Modifier = Modifier
) {
    val state by vm.uiState.collectAsState()

    // One-shot effect: ViewModel navigation e bolche, navigate UI korbe.
    LaunchedEffect(vm) {
        vm.effects.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToMain -> onLoginSuccess()
            }
        }
    }

    LoginContent(
        state = state,
        onEmailChange = vm::onEmailChange,
        onPasswordChange = vm::onPasswordChange,
        onTogglePassword = vm::onTogglePassword,
        onDismissDialog = vm::onDialogDismiss,
        onLoginClick = vm::onLoginClick,
        onRegisterClick = onNavigateToRegister,
        modifier = modifier
    )
}

@Composable
fun LoginContent(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onDismissDialog: () -> Unit = {},
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.dialogMessage != null) {
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = {
                Text(text = state.dialogTitle ?: "Notice", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = state.dialogMessage)
            },
            confirmButton = {
                TextButton(onClick = onDismissDialog) {
                    Text("OK", color = colorResource(R.color.trsp_red), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(32.dp))

        // ---- Logo + title ----
        Row(verticalAlignment = Alignment.CenterVertically) {
            BoxLogo()
            Spacer(Modifier.height(0.dp))
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("TRSP Scientific", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Dictionary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.trsp_red))
            }
        }

        Spacer(Modifier.height(32.dp))

        Text("Welcome back 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Login to continue learning", fontSize = 14.sp, color = Color.Gray)

        Spacer(Modifier.height(24.dp))

        // ---- Email ----
        AppTextField(
            label = "Email",
            value = state.email,
            onValueChange = onEmailChange,
            placeholder = "you@example.com",
            enabled = !state.isLoading,
            error = state.emailError,
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(12.dp))

        // ---- Password ----
        AppTextField(
            label = "Password",
            value = state.password,
            onValueChange = onPasswordChange,
            placeholder = "••••••••",
            enabled = !state.isLoading,
            error = state.passwordError,
            isPassword = true,
            passwordVisible = state.passwordVisible,
            onTogglePassword = onTogglePassword,
            keyboardType = KeyboardType.Password
        )

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { /* TODO: forgot password */ }, modifier = Modifier.align(Alignment.End)) {
            Text("Forgot password?", color = colorResource(R.color.trsp_red), fontSize = 13.sp)
        }

        Spacer(Modifier.height(8.dp))

        // ---- Login button ----
        PrimaryButton(
            text = "Login",
            onClick = onLoginClick,
            enabled = !state.isLoading,
            isLoading = state.isLoading
        )

        Spacer(Modifier.height(16.dp))

        // ---- Signup row ----
        Row(modifier = Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
            Text("New here? ", fontSize = 14.sp, color = Color.Gray)
            Text(
                "Create account",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.trsp_red),
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }

        Spacer(Modifier.height(24.dp))

        // ---- Skip (demo) ----
        TextButton(onClick = onLoginClick, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Skip for now →", color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Composable
private fun BoxLogo() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(colorResource(R.color.trsp_red)),
        contentAlignment = Alignment.Center
    ) {
        Text("T", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Preview(showBackground = true, name = "Login Default")
@Composable
fun LoginScreenPreview() {
    Kotlin_trsp_dictionary_appTheme {
        LoginContent(
            state = LoginUiState(
                email = "you@example.com",
                password = "password123"
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onDismissDialog = {},
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Login Loading Progress")
@Composable
fun LoginScreenLoadingPreview() {
    Kotlin_trsp_dictionary_appTheme {
        LoginContent(
            state = LoginUiState(
                email = "you@example.com",
                password = "password123",
                isLoading = true
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onDismissDialog = {},
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}
