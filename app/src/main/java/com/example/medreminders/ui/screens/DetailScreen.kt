package com.example.medreminders.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import com.example.medreminders.data.Task
import com.example.medreminders.data.TaskType
import com.example.medreminders.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    task: Task,
    onBack: () -> Unit,
    onDelete: (String) -> Unit,
    onEdit: (Task) -> Unit,
) {
    val isMed          = task.type == TaskType.MEDICATION
    val accent         = if (isMed) Green  else Navy
    val accentBg       = if (isMed) GreenLight else NavyLight
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onDelete(task.id)
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Detalle",
                            color      = Surface,
                            fontWeight = FontWeight.Bold,
                            style      = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            if (isMed) "Medicamento" else "Cita médica",
                            color = Surface.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                },
                navigationIcon = {
                    Row(
                        modifier = Modifier
                            .height(48.dp)
                            .padding(start = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick  = onBack,
                            modifier = Modifier
                                .size(48.dp)
                                .semantics { contentDescription = "Volver a la pantalla anterior" },
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Surface)
                        }
                        Text("Volver", color = Surface, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(end = 8.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy),
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick  = { onEdit(task) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "Editar este recordatorio" },
                        shape  = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy),
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Editar recordatorio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick  = { showDeleteDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .semantics { contentDescription = "Eliminar este recordatorio" },
                        shape  = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Red),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Red),
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Eliminar recordatorio", fontSize = 17.sp, fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Hero card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentBg)
                    .border(2.dp, accent.copy(alpha = 0.13f), RoundedCornerShape(14.dp))
                    .padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier         = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(accent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector  = if (isMed) Icons.Default.MedicalServices else Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint         = Surface,
                            modifier     = Modifier.size(28.dp),
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = accent.copy(alpha = 0.13f),
                    ) {
                        Text(
                            text     = if (isMed) "Medicamento" else "Cita médica",
                            style    = MaterialTheme.typography.labelSmall,
                            color    = accent,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text       = task.name,
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            // Detail rows
            Surface(
                shape = RoundedCornerShape(14.dp),
                border= androidx.compose.foundation.BorderStroke(1.dp, Border),
                color = Surface,
            ) {
                Column {
                    DetailRow("📅", "Fecha",  task.dateDisplay)
                    HorizontalDivider(color = Border)
                    DetailRow("🕐", "Hora",   task.timeDisplay)
                    if (task.dose != null) {
                        HorizontalDivider(color = Border)
                        DetailRow("💊", "Dosis", task.dose)
                    }
                    HorizontalDivider(color = Border)
                    DetailRow(
                        emoji      = "🔔",
                        label      = "Alarma",
                        value      = if (task.alarmEnabled) "Activada" else "Desactivada",
                        valueColor = if (task.alarmEnabled) Green else TextMuted,
                    )
                    if (task.notes != null) {
                        HorizontalDivider(color = Border)
                        DetailRow("📝", "Notas", task.notes, isLast = true)
                    }
                }
            }

            // Info notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NavyLight)
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text  = "Usa los botones de abajo para editar o eliminar este recordatorio.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    emoji: String,
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
    isLast: Boolean = false,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(emoji, fontSize = 22.sp, modifier = Modifier.width(30.dp))
        Column {
            Text(
                text     = label.uppercase(),
                style    = MaterialTheme.typography.labelSmall,
                color    = TextMuted,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text       = value,
                style      = MaterialTheme.typography.titleMedium,
                color      = valueColor,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun DeleteConfirmDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Red, modifier = Modifier.size(32.dp))
        },
        title = {
            Text(
                "¿Eliminar recordatorio?",
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
            )
        },
        text = {
            Text(
                "Esta acción no se puede deshacer.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
            )
        },
        dismissButton = {
            OutlinedButton(
                onClick  = onDismiss,
                modifier = Modifier.height(48.dp).semantics { contentDescription = "No, cancelar eliminación" },
                shape    = RoundedCornerShape(12.dp),
            ) { Text("No, cancelar") }
        },
        confirmButton = {
            Button(
                onClick  = onConfirm,
                modifier = Modifier.height(48.dp).semantics { contentDescription = "Sí, eliminar definitivamente" },
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Red),
            ) { Text("Sí, eliminar", fontWeight = FontWeight.Bold) }
        },
        shape             = RoundedCornerShape(16.dp),
        containerColor    = Surface,
    )
}
