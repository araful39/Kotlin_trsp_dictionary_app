package com.example.kotlin_trsp_dictionary_app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_trsp_dictionary_app.data.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Login screen er immutable state. UI sudhu ei ta collect korbe.
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val dialogTitle: String? = null,
    val dialogMessage: String? = null
)

// One-shot effect: ekbar ghatbe, state e save hoy na (jemon navigation).
sealed interface LoginEffect {
    data object NavigateToMain : LoginEffect
}

// VIEWMODEL (VM in MVVM): UI theke event newe, repository theke data niye
// notun state / effect pathay. Navigation logic ViewModel e thake na - effect pathay.
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Effect: UI (LaunchedEffect) collect kore navigate kore.
    private val _effects = Channel<LoginEffect>(Channel.BUFFERED)
    val effects: Flow<LoginEffect> = _effects.receiveAsFlow()

    // Dialog OK click e success hole navigate hobe.
    private var pendingNavigate = false

    fun onEmailChange(v: String) {
        _uiState.update { it.copy(email = v, emailError = null) }
    }

    fun onPasswordChange(v: String) {
        _uiState.update { it.copy(password = v, passwordError = null) }
    }

    fun onTogglePassword() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    // Dialog dismiss korle - success chilo hole NavigateToMain effect patano hoy.
    fun onDialogDismiss() {
        val navigate = pendingNavigate
        pendingNavigate = false
        _uiState.update { it.copy(dialogTitle = null, dialogMessage = null) }
        if (navigate) {
            viewModelScope.launch { _effects.send(LoginEffect.NavigateToMain) }
        }
    }

    private fun validate(): Boolean {
        val s = _uiState.value
        val emailError = when {
            s.email.isBlank() -> "Email required"
            "@" !in s.email -> "Enter a valid email"
            else -> null
        }
        val passwordError = when {
            s.password.isBlank() -> "Password required"
            s.password.length < 6 -> "Minimum 6 characters"
            else -> null
        }
        _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
        return emailError == null && passwordError == null
    }

    // Login button press (user intent). Valid hole repository call.
    fun onLoginClick() {
        if (!validate()) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = authRepository.login(_uiState.value.email.trim(), _uiState.value.password)
            result.fold(
                onSuccess = { response ->
                    if (response.error != null) {
                        _uiState.update {
                            it.copy(isLoading = false, dialogTitle = "Login Failed", dialogMessage = response.error)
                        }
                    } else {
                        pendingNavigate = true
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                dialogTitle = "Success",
                                dialogMessage = response.message ?: "Login successful!"
                            )
                        }
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dialogTitle = "Error",
                            dialogMessage = throwable.localizedMessage ?: "Network error. Please try again."
                        )
                    }
                }
            )
        }
    }
}
