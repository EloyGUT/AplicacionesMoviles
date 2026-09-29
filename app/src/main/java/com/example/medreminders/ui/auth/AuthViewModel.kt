package com.example.medreminders.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val registeredName: String = "",
    val registeredEmail: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val confirmPasswordError: String? = null,
)

sealed interface AuthResult {
    data class Success(val message: String) : AuthResult
    data class Error(val message: String) : AuthResult
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth? by lazy { runCatching { FirebaseAuth.getInstance() }.getOrNull() }
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        auth?.currentUser?.let { user ->
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                registeredEmail = user.email.orEmpty(),
            )
        }
    }

    fun login(email: String, password: String, onResult: (AuthResult) -> Unit) {
        val cleanEmail = email.trim()
        val emailError = validateEmail(cleanEmail)
        val passwordError = validatePassword(password)
        _uiState.value = _uiState.value.copy(
            emailError = emailError,
            passwordError = passwordError,
            nameError = null,
            confirmPasswordError = null,
        )
        if (emailError != null || passwordError != null) {
            onResult(AuthResult.Error("Revisa los datos indicados"))
            return
        }
        val firebaseAuth = auth ?: run {
            onResult(AuthResult.Error("Firebase aún no está configurado en esta app"))
            return
        }
        firebaseAuth.signInWithEmailAndPassword(cleanEmail, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    registeredEmail = firebaseAuth.currentUser?.email.orEmpty(),
                    emailError = null,
                    passwordError = null,
                )
                onResult(AuthResult.Success("Inicio de sesión correcto"))
            } else {
                onResult(AuthResult.Error("El correo o la contraseña son incorrectos"))
            }
        }
    }

    fun createAccount(name: String, email: String, password: String, confirmPassword: String, onResult: (AuthResult) -> Unit) {
        val cleanName = name.trim()
        val cleanEmail = email.trim()
        val nameError = if (cleanName.length < 2) "Escribe tu nombre completo" else null
        val emailError = validateEmail(cleanEmail)
        val passwordError = validatePassword(password)
        val confirmError = when {
            confirmPassword.isBlank() -> "Confirma tu contraseña"
            password != confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
        _uiState.value = _uiState.value.copy(
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmError,
        )
        if (listOf(nameError, emailError, passwordError, confirmError).any { it != null }) {
            onResult(AuthResult.Error("Revisa los datos indicados"))
            return
        }
        val firebaseAuth = auth ?: run {
            onResult(AuthResult.Error("Firebase aún no está configurado en esta app"))
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(cleanEmail, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _uiState.value = AuthUiState(
                    isAuthenticated = true,
                    registeredName = cleanName,
                    registeredEmail = firebaseAuth.currentUser?.email.orEmpty(),
                )
                onResult(AuthResult.Success("Cuenta creada correctamente"))
            } else {
                onResult(AuthResult.Error("No fue posible crear la cuenta. Revisa el correo e inténtalo de nuevo"))
            }
        }
    }

    fun logout() {
        auth?.signOut()
        _uiState.value = _uiState.value.copy(isAuthenticated = false)
    }

    fun clearErrors() {
        _uiState.value = _uiState.value.copy(
            emailError = null, passwordError = null, nameError = null, confirmPasswordError = null,
        )
    }

    private fun validateEmail(email: String) = when {
        email.isBlank() -> "Escribe tu correo electrónico"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Escribe un correo electrónico válido"
        else -> null
    }

    private fun validatePassword(password: String) = when {
        password.isBlank() -> "Escribe tu contraseña"
        password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
        else -> null
    }
}
