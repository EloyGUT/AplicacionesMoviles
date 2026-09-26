package com.example.medreminders.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.CalendarMonth
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
fun HomeScreen(
    tasks: List<Task>,
    toast: String?,
    onClearToast: () -> Unit,
    onSelectTask: (Task) -> Unit,
    onNewTask: () -> Unit,
) {
    val todayMeds  = tasks.filter { it.dateDisplay == "Hoy" && it.type == TaskType.MEDICATION }
    val todayAppts = tasks.filter { it.dateDisplay == "Hoy" && it.type == TaskType.APPOINTMENT }
    val upcoming   = tasks.filter { it.dateDisplay != "Hoy" }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toast) {
        if (toast != null) {
            snackbarHostState.showSnackbar(toast)
            onClearToast()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Mis Recordatorios",
                            style      = MaterialTheme.typography.titleLarge,
                            color      = Surface,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text  = "Jueves, 28 de agosto de 2026",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Surface.copy(alpha = 0.75f),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy),
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick  = onNewTask,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(56.dp)
                        .semantics { contentDescription = "Agregar nuevo recordatorio" },
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Navy),
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text     = "Agregar recordatorio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
        containerColor = Background,
    ) { padding ->

        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {

            // Summary banner
            item {
                SummaryBanner(count = todayMeds.size + todayAppts.size)
                Spacer(Modifier.height(20.dp))
            }

            // Today medications
            if (todayMeds.isNotEmpty()) {
                item {
                    SectionHeader(
                        label     = "Medicamentos de hoy",
                        color     = Green,
                        bgColor   = GreenLight,
                    )
                }
                items(todayMeds) { task ->
                    TaskCard(task = task, onPress = { onSelectTask(task) }, isLast = task == todayMeds.last())
                }
                item { Spacer(Modifier.height(20.dp)) }
            }

            // Today appointments
            if (todayAppts.isNotEmpty()) {
                item {
                    SectionHeader(
                        label   = "Citas de hoy",
                        color   = Navy,
                        bgColor = NavyLight,
                    )
                }
                items(todayAppts) { task ->
                    TaskCard(task = task, onPress = { onSelectTask(task) }, isLast = task == todayAppts.last())
                }
                item { Spacer(Modifier.height(20.dp)) }
            }

            // Upcoming
            if (upcoming.isNotEmpty()) {
                item {
                    SectionHeader(
                        label   = "Próximamente",
                        color   = Amber,
                        bgColor = AmberLight,
                    )
                }
                items(upcoming) { task ->
                    TaskCard(task = task, onPress = { onSelectTask(task) }, isLast = task == upcoming.last())
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun SummaryBanner(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NavyLight)
            .padding(start = 18.dp, top = 14.dp, end = 18.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Navy),
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text       = "Tienes $count recordatorios para hoy",
                style      = MaterialTheme.typography.titleMedium,
                color      = Navy,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text  = "Toca cualquiera para ver el detalle",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun SectionHeader(label: String, color: Color, bgColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelLarge,
            color      = color,
            fontWeight = FontWeight.Bold,
            fontSize   = 15.sp,
        )
    }
}

@Composable
private fun TaskCard(task: Task, onPress: () -> Unit, isLast: Boolean) {
    val isMed   = task.type == TaskType.MEDICATION
    val accent  = if (isMed) Green else Navy
    val accentBg= if (isMed) GreenLight else NavyLight

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .clickable(
                onClickLabel = "Ver detalle de ${task.name}",
                onClick      = onPress,
            )
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 72.dp)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Type icon
            Box(
                modifier          = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentBg),
                contentAlignment  = Alignment.Center,
            ) {
                Icon(
                    imageVector  = if (isMed) Icons.Default.MedicalServices else Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint         = accent,
                    modifier     = Modifier.size(26.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = task.name,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines   = 1,
                )
                Spacer(Modifier.height(3.dp))
                val sub = buildString {
                    if (isMed && task.dose != null) append("${task.dose}  ·  ")
                    append(task.timeDisplay)
                    append("  ·  ")
                    append(task.dateDisplay)
                }
                Text(text = sub, style = MaterialTheme.typography.bodyMedium)
            }

            if (task.alarmEnabled) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "Alarma activada",
                    tint     = Amber,
                    modifier = Modifier.size(22.dp),
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint     = Border,
                modifier = Modifier.size(20.dp),
            )
        }

        if (!isLast) {
            HorizontalDivider(color = Border, thickness = 1.dp)
        }
    }
}
