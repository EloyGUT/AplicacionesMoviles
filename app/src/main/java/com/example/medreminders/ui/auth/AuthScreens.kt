package com.example.medreminders.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medreminders.ui.theme.Navy

@Composable
fun LoginScreen(authViewModel: AuthViewModel, onLoginSuccess: () -> Unit, onCreateAccount: () -> Unit) {
    val context = LocalContext.current
    val state by authViewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    fun submit() {
        authViewModel.login(email, password) { result ->
            when (result) {
                is AuthResult.Success -> { Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show(); onLoginSuccess() }
                is AuthResult.Error -> Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    AuthScaffold(title = "Recordatorios médicos", subtitle = "Inicia sesión para consultar tus medicamentos y próximas citas.") {
        AuthField(email, { email = it; authViewModel.clearErrors() }, "Correo electrónico", "nombre@correo.com", state.emailError, KeyboardType.Email, ImeAction.Next)
        Spacer(Modifier.height(20.dp))
        AuthField(password, { password = it; authViewModel.clearErrors() }, "Contraseña", "Escribe tu contraseña", state.passwordError, KeyboardType.Password, ImeAction.Done, true, ::submit)
        Spacer(Modifier.height(28.dp))
        AuthButton("Iniciar sesión", ::submit)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onCreateAccount, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Crear una cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(24.dp))
        Text("Si necesitas ayuda, pídele apoyo a un familiar o cuidador.", color = Color(0xFF555555), fontSize = 16.sp)
    }
}

@Composable
fun CreateAccountScreen(authViewModel: AuthViewModel, onAccountCreated: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val state by authViewModel.uiState.collectAsState()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    fun submit() {
        authViewModel.createAccount(name, email, password, confirmPassword) { result ->
            when (result) {
                is AuthResult.Success -> { Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show(); onAccountCreated() }
                is AuthResult.Error -> Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    AuthScaffold(title = "Crear cuenta", subtitle = "Completa tus datos para guardar tus recordatorios médicos.") {
        AuthField(name, { name = it; authViewModel.clearErrors() }, "Nombre completo", "Escribe tu nombre", state.nameError, KeyboardType.Text, ImeAction.Next)
        Spacer(Modifier.height(16.dp))
        AuthField(email, { email = it; authViewModel.clearErrors() }, "Correo electrónico", "nombre@correo.com", state.emailError, KeyboardType.Email, ImeAction.Next)
        Spacer(Modifier.height(16.dp))
        AuthField(password, { password = it; authViewModel.clearErrors() }, "Contraseña", "Mínimo 6 caracteres", state.passwordError, KeyboardType.Password, ImeAction.Next, true)
        Spacer(Modifier.height(16.dp))
        AuthField(confirmPassword, { confirmPassword = it; authViewModel.clearErrors() }, "Confirmar contraseña", "Escribe nuevamente tu contraseña", state.confirmPasswordError, KeyboardType.Password, ImeAction.Done, true, ::submit)
        Spacer(Modifier.height(28.dp))
        AuthButton("Crear mi cuenta", ::submit)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Volver al inicio de sesión", fontSize = 17.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun AuthScaffold(title: String, subtitle: String, content: @Composable () -> Unit) = Scaffold(containerColor = Color.White) { padding ->
    Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text(title, color = Navy, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp)); Text(subtitle, fontSize = 19.sp, lineHeight = 27.sp)
        Spacer(Modifier.height(30.dp)); content()
    }
}

@Composable
private fun AuthField(value: String, onChange: (String) -> Unit, label: String, placeholder: String, error: String?, type: KeyboardType, action: ImeAction, password: Boolean = false, onDone: () -> Unit = {}) {
    Text(label, color = Navy, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(value, onChange, Modifier.fillMaxWidth().semantics { contentDescription = label }, placeholder = { Text(placeholder) }, singleLine = true, isError = error != null, visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None, keyboardOptions = KeyboardOptions(keyboardType = type, imeAction = action), keyboardActions = KeyboardActions(onDone = { onDone() }), supportingText = error?.let { { Text(it) } }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Navy, cursorColor = Navy))
}

@Composable
private fun AuthButton(label: String, onClick: () -> Unit) = Button(onClick, Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Navy)) { Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
