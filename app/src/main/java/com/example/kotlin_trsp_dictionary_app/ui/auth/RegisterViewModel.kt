package com.example.kotlin_trsp_dictionary_app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_trsp_dictionary_app.data.model.CLASS_OPTIONS
import com.example.kotlin_trsp_dictionary_app.data.model.ClassOption
import com.example.kotlin_trsp_dictionary_app.data.repository.AuthRepository
import com.example.kotlin_trsp_dictionary_app.data.repository.ClassRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Register screen er immutable state.
data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val classId: String = CLASS_OPTIONS[1].value, // default: class-9-10
    val classOptions: List<ClassOption> = CLASS_OPTIONS, // API fail hole fallback list
    val classesLoading: Boolean = false,
    val passwordVisible: Boolean = false,
    val confirmVisible: Boolean = false,
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val dialogTitle: String? = null,
    val dialogMessage: String? = null
)

// One-shot effect: ekbar ghatbe (jemon navigation).
sealed interface RegisterEffect {
    data object NavigateToMain : RegisterEffect   // token peyechi -> direct home
    data object NavigateToLogin : RegisterEffect  // token nei -> login e phiro, kore login
}

// Register ViewModel: form state + validation + API call.
// UI sudhu state dekhbe, event pathabe, effect collect korbe.
class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _effects = Channel<RegisterEffect>(Channel.BUFFERED)
    val effects: Flow<RegisterEffect> = _effects.receiveAsFlow()

    // Screen khular sathe-sathe class list API theke anbo.
    init {
        loadClasses()
    }

    // GET api/classes -> dropdown list. Fail hole fallback (CLASS_OPTIONS) thakbe.
    private fun loadClasses() {
        _uiState.update { it.copy(classesLoading = true) }
        viewModelScope.launch {
            classRepository.getClasses().fold(
                onSuccess = { options ->
                    _uiState.update { s ->
                        s.copy(
                            classesLoading = false,
                            classOptions = if (options.isEmpty()) s.classOptions else options,
                            // selected class jodi notun list e na thake, prothom ta boshao
                            classId = if (options.any { it.value == s.classId }) {
                                s.classId
                            } else {
                                options.firstOrNull()?.value ?: s.classId
                            }
                        )
                    }
                },
                onFailure = {
                    // Internet/server problem - fallback list diye kaj chalao.
                    _uiState.update { it.copy(classesLoading = false) }
                }
            )
        }
    }

    // Dialog OK click e - kothay jabo seta (null hole kothao jabo na).
    private var pendingEffect: RegisterEffect? = null

    fun onNameChange(v: String) {
        _uiState.update { it.copy(name = v, nameError = null) }
    }

    fun onEmailChange(v: String) {
        _uiState.update { it.copy(email = v, emailError = null) }
    }

    fun onPhoneChange(v: String) {
        _uiState.update { it.copy(phone = v, phoneError = null) }
    }

    fun onClassChange(v: String) {
        _uiState.update { it.copy(classId = v) }
    }

    fun onPasswordChange(v: String) {
        _uiState.update { s ->
            s.copy(
                password = v,
                passwordError = null,
                // confirm mismatch clear koro jodi ekhon mile jay
                confirmError = if (s.confirmPassword.isNotEmpty() && v == s.confirmPassword) null else s.confirmError
            )
        }
    }

    fun onConfirmChange(v: String) {
        _uiState.update { it.copy(confirmPassword = v, confirmError = null) }
    }

    fun onTogglePassword() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onToggleConfirm() {
        _uiState.update { it.copy(confirmVisible = !it.confirmVisible) }
    }

    // Dialog dismiss korle - success chilo hole tahole effect pathano hoy.
    fun onDialogDismiss() {
        val effect = pendingEffect
        pendingEffect = null
        _uiState.update { it.copy(dialogTitle = null, dialogMessage = null) }
        if (effect != null) {
            viewModelScope.launch { _effects.send(effect) }
        }
    }

    private fun validate(): Boolean {
        val s = _uiState.value
        val nameError = when {
            s.name.isBlank() -> "Name required"
            s.name.trim().length < 3 -> "Minimum 3 characters"
            else -> null
        }
        val emailError = when {
            s.email.isBlank() -> "Email required"
            "@" !in s.email -> "Enter a valid email"
            else -> null
        }
        val phoneError = when {
            s.phone.isBlank() -> "Phone required"
            s.phone.filter { it.isDigit() }.length < 11 -> "Enter a valid phone number"
            else -> null
        }
        val passwordError = when {
            s.password.isBlank() -> "Password required"
            s.password.length < 6 -> "Minimum 6 characters"
            else -> null
        }
        val confirmError = when {
            s.confirmPassword.isBlank() -> "Confirm your password"
            s.confirmPassword != s.password -> "Passwords do not match"
            else -> null
        }
        _uiState.update {
            it.copy(
                nameError = nameError,
                emailError = emailError,
                phoneError = phoneError,
                passwordError = passwordError,
                confirmError = confirmError
            )
        }
        return nameError == null && emailError == null && phoneError == null &&
            passwordError == null && confirmError == null
    }

    // Register button press (user intent). Valid hole repository call.
    fun onRegisterClick() {
        if (!validate()) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val s = _uiState.value
            val result = authRepository.register(
                name = s.name.trim(),
                email = s.email.trim(),
                phone = s.phone.trim(),
                password = s.password,
                classId = s.classId
            )
            result.fold(
                onSuccess = { response ->
                    if (response.error != null) {
                        pendingEffect = null
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                dialogTitle = "Registration Failed",
                                dialogMessage = response.error // server er asol error
                            )
                        }
                    } else {
                        // Token pele direct home, na hole login e phiro.
                        pendingEffect = if (response.token != null) {
                            RegisterEffect.NavigateToMain
                        } else {
                            RegisterEffect.NavigateToLogin
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                dialogTitle = "Success",
                                dialogMessage = response.message ?: "Account created!"
                            )
                        }
                    }
                },
                onFailure = { throwable ->
                    pendingEffect = null
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
