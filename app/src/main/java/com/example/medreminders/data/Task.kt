package com.example.medreminders.data

import java.util.UUID

enum class TaskType {
    MEDICATION,
    APPOINTMENT
}

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val type: TaskType,
    val name: String,
    val timeDisplay: String,
    val dateDisplay: String,
    val dose: String? = null,
    val alarmEnabled: Boolean = true,
    val notes: String? = null,
)

val sampleTasks = listOf(
    Task(
        type = TaskType.MEDICATION,
        name = "Metformina 500mg",
        timeDisplay = "08:00 AM",
        dateDisplay = "Hoy",
        dose = "1 pastilla",
        alarmEnabled = true,
        notes = "Tomar con el desayuno",
    ),

    Task(
        type = TaskType.MEDICATION,
        name = "Losartán 50mg",
        timeDisplay = "08:00 AM",
        dateDisplay = "Hoy",
        dose = "1 pastilla",
        alarmEnabled = true,
        notes = "Tomar con agua",
    ),

    Task(
        type = TaskType.APPOINTMENT,
        name = "Dr. García – Cardiología",
        timeDisplay = "10:30 AM",
        dateDisplay = "Mañana",
        alarmEnabled = true,
        notes = "Clínica Central, piso 3. Llevar carnet.",
    ),

    Task(
        type = TaskType.MEDICATION,
        name = "Atorvastatina 20mg",
        timeDisplay = "09:00 PM",
        dateDisplay = "Hoy",
        dose = "1 pastilla",
        alarmEnabled = true,
    ),

    Task(
        type = TaskType.APPOINTMENT,
        name = "Análisis de sangre",
        timeDisplay = "07:30 AM",
        dateDisplay = "Vie 30 Ago",
        alarmEnabled = true,
        notes = "Laboratorio San Juan · Ayuno de 8 horas",
    ),
)