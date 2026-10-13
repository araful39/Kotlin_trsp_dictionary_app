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
import com.example.kotlin_trsp_dictionary_app.ui.components.AppDropdown
import com.example.kotlin_trsp_dictionary_app.ui.components.AppTextField
import com.example.kotlin_trsp_dictionary_app.ui.components.PrimaryButton
import com.example.kotlin_trsp_dictionary_app.ui.theme.Kotlin_trsp_dictionary_appTheme

// 4. VIEW (V in MVVM): state collect kore, event ViewModel e pathay,
// effect (navigate) collect kore. Logic UI te nai.
// Register screen: form + API call (POST /api/auth/register).
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    vm: RegisterViewModel = viewModel(factory = AppViewModelFactory),
    modifier: Modifier = Modifier
) {
    val state by vm.uiState.collectAsState()

    // One-shot effect: 2 rokom navigate - token pele home, na hole login.
    LaunchedEffect(vm) {
        vm.effects.collect { effect ->
            when (effect) {
                RegisterEffect.NavigateToMain -> onRegisterSuccess()
                RegisterEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    RegisterContent(
        state = state,
        onNameChange = vm::onNameChange,
        onEmailChange = vm::onEmailChange,
        onPhoneChange = vm::onPhoneChange,
        onClassChange = vm::onClassChange,
        onPasswordChange = vm::onPasswordChange,
        onConfirmChange = vm::onConfirmChange,
        onTogglePassword = vm::onTogglePassword,
        onToggleConfirm = vm::onToggleConfirm,
        onDismissDialog = vm::onDialogDismiss,
        onRegisterClick = vm::onRegisterClick,
        onLoginClick = onNavigateToLogin,
        modifier = modifier
    )
}

@Composable
fun RegisterContent(
    state: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onClassChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleConfirm: () -> Unit,
    onDismissDialog: () -> Unit = {},
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
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
            RegisterLogo()
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("TRSP Scientific", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Dictionary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.trsp_red))
            }
        }

        Spacer(Modifier.height(32.dp))

        Text("Create account 🎓", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Join to explore science terms", fontSize = 14.sp, color = Color.Gray)

        Spacer(Modifier.height(24.dp))

        // ---- Name ----
        AppTextField(
            label = "Full Name",
            value = state.name,
            onValueChange = onNameChange,
            placeholder = "Your name",
            enabled = !state.isLoading,
            error = state.nameError,
            keyboardType = KeyboardType.Text
        )

        Spacer(Modifier.height(12.dp))

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

        // ---- Phone ----
        AppTextField(
            label = "Phone",
            value = state.phone,
            onValueChange = onPhoneChange,
            placeholder = "+8801711222333",
            enabled = !state.isLoading,
            error = state.phoneError,
            keyboardType = KeyboardType.Phone
        )

        Spacer(Modifier.height(12.dp))

        // ---- Class dropdown (list API theke ase) ----
        AppDropdown(
            label = "Class",
            value = state.classId,
            options = state.classOptions,
            onValueChange = onClassChange,
            enabled = !state.isLoading
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

        Spacer(Modifier.height(12.dp))

        // ---- Confirm password ----
        AppTextField(
            label = "Confirm Password",
            value = state.confirmPassword,
            onValueChange = onConfirmChange,
            placeholder = "••••••••",
            enabled = !state.isLoading,
            error = state.confirmError,
            isPassword = true,
            passwordVisible = state.confirmVisible,
            onTogglePassword = onToggleConfirm,
            keyboardType = KeyboardType.Password
        )

        Spacer(Modifier.height(20.dp))

        // ---- Register button (API call) ----
        PrimaryButton(
            text = "Create Account",
            onClick = onRegisterClick,
            enabled = !state.isLoading,
            isLoading = state.isLoading
        )

        Spacer(Modifier.height(16.dp))

        // ---- Login row ----
        Row(modifier = Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account? ", fontSize = 14.sp, color = Color.Gray)
            Text(
                "Login",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.trsp_red),
                modifier = Modifier.clickable { onLoginClick() }
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun RegisterLogo() {
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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    Kotlin_trsp_dictionary_appTheme {
        RegisterContent(
            state = RegisterUiState(
                name = "New Student",
                email = "new.student@example.com",
                phone = "+8801711222333"
            ),
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onClassChange = {},
            onPasswordChange = {},
            onConfirmChange = {},
            onTogglePassword = {},
            onToggleConfirm = {},
            onDismissDialog = {},
            onRegisterClick = {},
            onLoginClick = {}
        )
    }
}
