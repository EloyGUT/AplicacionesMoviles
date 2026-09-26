package com.example.medreminders.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medreminders.data.Task
import com.example.medreminders.data.TaskType
import com.example.medreminders.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    onBack: () -> Unit,
    onSave: (Task) -> Unit,
) {
    var type    by remember { mutableStateOf(TaskType.MEDICATION) }
    var name    by remember { mutableStateOf("") }
    var dose    by remember { mutableStateOf("") }
    var date    by remember { mutableStateOf("29/08/2026") }
    var time    by remember { mutableStateOf("08:00") }
    var alarm   by remember { mutableStateOf(true) }
    var notes   by remember { mutableStateOf("") }
    var touched by remember { mutableStateOf(false) }

    val isValid   = name.trim().isNotEmpty()
    val nameError = touched && !isValid
    val isMed     = type == TaskType.MEDICATION

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Nuevo Recordatorio",
                            color      = Surface,
                            fontWeight = FontWeight.Bold,
                            style      = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            if (isMed) "Agregar medicamento" else "Agregar cita médica",
                            color = Surface.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick  = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Volver a la pantalla anterior" },
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Surface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy),
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick  = {
                            touched = true
                            if (isValid) onSave(
                                Task(
                                    type        = type,
                                    name        = name.trim(),
                                    timeDisplay = time,
                                    dateDisplay = "Próximamente",
                                    dose        = dose.ifBlank { null },
                                    alarmEnabled= alarm,
                                    notes       = notes.ifBlank { null },
                                )
                            )
                        },
                        enabled  = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "Guardar recordatorio" },
                        shape  = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor         = Green,
                            disabledContainerColor = Border,
                        ),
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Guardar recordatorio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick  = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .semantics { contentDescription = "Cancelar y volver" },
                        shape  = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Border),
                    ) {
                        Text("Cancelar", fontSize = 16.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                    }
                }
            }
        },
        containerColor = Background,
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {

            // Type selector
            FieldLabel("Tipo de recordatorio", required = true)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TypeButton(
                    label    = "Medicamento",
                    active   = isMed,
                    color    = Green,
                    modifier = Modifier.weight(1f),
                    onClick  = { type = TaskType.MEDICATION },
                )
                TypeButton(
                    label    = "Cita médica",
                    active   = !isMed,
                    color    = Navy,
                    modifier = Modifier.weight(1f),
                    onClick  = { type = TaskType.APPOINTMENT },
                )
            }
            Spacer(Modifier.height(20.dp))

            // Name
            FieldLabel(
                text     = if (isMed) "Nombre del medicamento" else "Nombre de la cita",
                required = true,
                error    = if (nameError) "Este campo es obligatorio." else null,
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = name,
                onValueChange = { name = it; touched = true },
                placeholder   = { Text(if (isMed) "Ej: Metformina 500mg" else "Ej: Dr. García – Cardiología") },
                isError       = nameError,
                modifier      = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp)
                    .semantics { contentDescription = if (isMed) "Nombre del medicamento" else "Nombre de la cita" },
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = if (nameError) Red else Navy,
                    unfocusedBorderColor = if (nameError) Red else Border,
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
            )
            Spacer(Modifier.height(20.dp))

            // Dose (meds only)
            if (isMed) {
                FieldLabel("Dosis o instrucción")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value         = dose,
                    onValueChange = { dose = it },
                    placeholder   = { Text("Ej: 1 pastilla, 5 ml") },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Navy,
                        unfocusedBorderColor = Border,
                    ),
                    textStyle  = MaterialTheme.typography.bodyLarge,
                    singleLine = true,
                )
                Spacer(Modifier.height(20.dp))
            }

            // Date + Time
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("Fecha", required = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = date,
                        onValueChange = { date = it },
                        modifier      = Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Navy, unfocusedBorderColor = Border),
                        textStyle  = MaterialTheme.typography.bodyLarge,
                        singleLine = true,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("Hora", required = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = time,
                        onValueChange = { time = it },
                        modifier      = Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Navy, unfocusedBorderColor = Border),
                        textStyle  = MaterialTheme.typography.bodyLarge,
                        singleLine = true,
                    )
                }
            }
            Spacer(Modifier.height(20.dp))

            // Alarm toggle
            FieldLabel("Alarma de recordatorio")
            Spacer(Modifier.height(8.dp))
            AlarmToggle(alarm = alarm, onToggle = { alarm = !alarm })
            Spacer(Modifier.height(20.dp))

            // Notes
            FieldLabel("Notas adicionales")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = notes,
                onValueChange = { notes = it },
                placeholder   = { Text("Ej: Tomar con el desayuno, llevar documentos...") },
                modifier      = Modifier.fillMaxWidth().height(100.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Navy, unfocusedBorderColor = Border),
                textStyle = MaterialTheme.typography.bodyLarge,
                maxLines  = 4,
            )
            Spacer(Modifier.height(16.dp))

            // Helper tip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AmberLight)
                    .border(1.dp, Color(0xFFF5D78E), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text  = "Los campos con * son obligatorios. El botón Guardar se activa cuando completes el nombre.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Amber,
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TypeButton(label: String, active: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick  = onClick,
        modifier = modifier
            .height(64.dp)
            .semantics { contentDescription = label },
        shape  = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor         = if (active) color else Surface,
            contentColor           = if (active) Surface else TextMuted,
            disabledContainerColor = Border,
        ),
        border = if (!active) androidx.compose.foundation.BorderStroke(2.dp, Border) else null,
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun AlarmToggle(alarm: Boolean, onToggle: () -> Unit) {
    val borderColor = if (alarm) Green else Border
    val bgColor     = if (alarm) GreenLight else Color(0xFFF5F7FA)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 18.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(
            imageVector        = if (alarm) Icons.Default.Notifications else Icons.Default.NotificationsOff,
            contentDescription = null,
            tint               = if (alarm) Green else TextMuted,
            modifier           = Modifier.size(24.dp),
        )
        Text(
            text       = if (alarm) "Alarma ACTIVADA" else "Alarma desactivada",
            style      = MaterialTheme.typography.titleMedium,
            color      = if (alarm) Green else TextMuted,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.weight(1f),
        )
        Switch(
            checked         = alarm,
            onCheckedChange = { onToggle() },
            modifier        = Modifier.semantics { contentDescription = if (alarm) "Desactivar alarma" else "Activar alarma" },
            colors          = SwitchDefaults.colors(
                checkedThumbColor    = Surface,
                checkedTrackColor    = Green,
                uncheckedThumbColor  = Surface,
                uncheckedTrackColor  = Border,
            ),
        )
    }
}

@Composable
fun FieldLabel(text: String, required: Boolean = false, error: String? = null) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text       = text,
                style      = MaterialTheme.typography.labelLarge,
                color      = if (error != null) Red else Navy,
                fontWeight = FontWeight.Medium,
            )
            if (required) {
                Text(" *", color = Red, fontWeight = FontWeight.Bold)
            }
        }
        if (error != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text  = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Red,
            )
        }
    }
}
