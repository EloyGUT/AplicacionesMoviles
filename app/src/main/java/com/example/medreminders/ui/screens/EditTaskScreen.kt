package com.example.medreminders.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medreminders.data.Task
import com.example.medreminders.data.TaskType
import com.example.medreminders.ui.theme.Background
import com.example.medreminders.ui.theme.Border
import com.example.medreminders.ui.theme.Green
import com.example.medreminders.ui.theme.GreenLight
import com.example.medreminders.ui.theme.Navy
import com.example.medreminders.ui.theme.NavyLight
import com.example.medreminders.ui.theme.Surface
import com.example.medreminders.ui.theme.TextMuted
import com.example.medreminders.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    task: Task,
    onSave: (Task) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    var taskType by rememberSaveable(task.id) {
        mutableStateOf(task.type)
    }

    var name by rememberSaveable(task.id) {
        mutableStateOf(task.name)
    }

    var dateDisplay by rememberSaveable(task.id) {
        mutableStateOf(task.dateDisplay)
    }

    var timeDisplay by rememberSaveable(task.id) {
        mutableStateOf(task.timeDisplay)
    }

    var dose by rememberSaveable(task.id) {
        mutableStateOf(task.dose.orEmpty())
    }

    var notes by rememberSaveable(task.id) {
        mutableStateOf(task.notes.orEmpty())
    }

    var alarmEnabled by rememberSaveable(task.id) {
        mutableStateOf(task.alarmEnabled)
    }

    var nameError by rememberSaveable(task.id) {
        mutableStateOf<String?>(null)
    }

    var dateError by rememberSaveable(task.id) {
        mutableStateOf<String?>(null)
    }

    var timeError by rememberSaveable(task.id) {
        mutableStateOf<String?>(null)
    }

    var doseError by rememberSaveable(task.id) {
        mutableStateOf<String?>(null)
    }

    val accentColor = if (taskType == TaskType.MEDICATION) {
        Green
    } else {
        Navy
    }

    val accentBackground = if (taskType == TaskType.MEDICATION) {
        GreenLight
    } else {
        NavyLight
    }

    fun openDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                val formatter = SimpleDateFormat(
                    "EEE d MMM yyyy",
                    Locale("es", "ES"),
                )

                dateDisplay = formatter
                    .format(calendar.time)
                    .replaceFirstChar { character ->
                        if (character.isLowerCase()) {
                            character.titlecase(Locale("es", "ES"))
                        } else {
                            character.toString()
                        }
                    }

                dateError = null
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    fun openTimePicker() {
        val calendar = Calendar.getInstance()

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val formatter = SimpleDateFormat(
                    "hh:mm a",
                    Locale("es", "ES"),
                )

                timeDisplay = formatter
                    .format(calendar.time)
                    .uppercase(Locale("es", "ES"))

                timeError = null
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false,
        ).show()
    }

    fun validateAndSave() {
        val cleanName = name.trim()
        val cleanDate = dateDisplay.trim()
        val cleanTime = timeDisplay.trim()
        val cleanDose = dose.trim()
        val cleanNotes = notes.trim()

        nameError = when {
            cleanName.isBlank() -> {
                if (taskType == TaskType.MEDICATION) {
                    "Escribe el nombre del medicamento."
                } else {
                    "Escribe el nombre de la cita."
                }
            }

            cleanName.length < 2 -> "El nombre es demasiado corto."
            else -> null
        }

        dateError = if (cleanDate.isBlank()) {
            "Selecciona una fecha."
        } else {
            null
        }

        timeError = if (cleanTime.isBlank()) {
            "Selecciona una hora."
        } else {
            null
        }

        doseError = if (
            taskType == TaskType.MEDICATION &&
            cleanDose.isBlank()
        ) {
            "Escribe la dosis del medicamento."
        } else {
            null
        }

        val hasErrors = listOf(
            nameError,
            dateError,
            timeError,
            doseError,
        ).any { error -> error != null }

        if (hasErrors) {
            Toast.makeText(
                context,
                "Revisa los campos indicados.",
                Toast.LENGTH_SHORT,
            ).show()

            return
        }

        val updatedTask = task.copy(
            type = taskType,
            name = cleanName,
            dateDisplay = cleanDate,
            timeDisplay = cleanTime,
            dose = if (taskType == TaskType.MEDICATION) {
                cleanDose
            } else {
                null
            },
            alarmEnabled = alarmEnabled,
            notes = cleanNotes.ifBlank { null },
        )

        onSave(updatedTask)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Editar recordatorio",
                            color = Surface,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )

                        Text(
                            text = if (taskType == TaskType.MEDICATION) {
                                "Medicamento"
                            } else {
                                "Cita médica"
                            },
                            color = Surface.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics {
                                contentDescription = "Volver sin guardar los cambios"
                            },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Surface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy,
                ),
            )
        },
        bottomBar = {
            Surface(
                color = Surface,
                shadowElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Button(
                        onClick = { validateAndSave() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics {
                                contentDescription = "Guardar los cambios del recordatorio"
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Navy,
                            contentColor = Surface,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Guardar cambios",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .semantics {
                                contentDescription = "Cancelar la edición"
                            },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(2.dp, Border),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextMuted,
                        ),
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        },
        containerColor = Background,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            EditorSection(
                title = "Tipo de recordatorio",
                description = "Selecciona medicamento o cita médica.",
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TaskTypeButton(
                        text = "Medicamento",
                        selected = taskType == TaskType.MEDICATION,
                        selectedColor = Green,
                        onClick = {
                            taskType = TaskType.MEDICATION
                            doseError = null
                        },
                        modifier = Modifier.weight(1f),
                    )

                    TaskTypeButton(
                        text = "Cita médica",
                        selected = taskType == TaskType.APPOINTMENT,
                        selectedColor = Navy,
                        onClick = {
                            taskType = TaskType.APPOINTMENT
                            doseError = null
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            EditorSection(
                title = if (taskType == TaskType.MEDICATION) {
                    "Información del medicamento"
                } else {
                    "Información de la cita"
                },
                description = "Los campos marcados como obligatorios deben completarse.",
            ) {
                EditorTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = if (taskType == TaskType.MEDICATION) {
                        "Nombre del medicamento"
                    } else {
                        "Nombre de la cita"
                    },
                    placeholder = if (taskType == TaskType.MEDICATION) {
                        "Ejemplo: Metformina 500 mg"
                    } else {
                        "Ejemplo: Consulta de cardiología"
                    },
                    error = nameError,
                    imeAction = ImeAction.Next,
                )

                if (taskType == TaskType.MEDICATION) {
                    EditorTextField(
                        value = dose,
                        onValueChange = {
                            dose = it
                            doseError = null
                        },
                        label = "Dosis",
                        placeholder = "Ejemplo: 1 pastilla",
                        error = doseError,
                        imeAction = ImeAction.Next,
                    )
                }
            }

            EditorSection(
                title = "Fecha y hora",
                description = "Comprueba cuándo debe aparecer el recordatorio.",
            ) {
                SelectionField(
                    label = "Fecha",
                    value = dateDisplay,
                    buttonText = "Seleccionar fecha",
                    error = dateError,
                    onClick = { openDatePicker() },
                )

                SelectionField(
                    label = "Hora",
                    value = timeDisplay,
                    buttonText = "Seleccionar hora",
                    error = timeError,
                    onClick = { openTimePicker() },
                )
            }

            EditorSection(
                title = "Alarma",
                description = "La alarma te avisará sobre este recordatorio.",
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = if (alarmEnabled) {
                                "Alarma activada"
                            } else {
                                "Alarma desactivada"
                            },
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = if (alarmEnabled) {
                                "Recibirás un aviso a la hora indicada."
                            } else {
                                "No recibirás un aviso para este recordatorio."
                            },
                            color = TextMuted,
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                        )
                    }

                    Switch(
                        checked = alarmEnabled,
                        onCheckedChange = {
                            alarmEnabled = it
                        },
                        modifier = Modifier.semantics {
                            contentDescription = if (alarmEnabled) {
                                "Alarma activada"
                            } else {
                                "Alarma desactivada"
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Surface,
                            checkedTrackColor = accentColor,
                            uncheckedThumbColor = Surface,
                            uncheckedTrackColor = Border,
                        ),
                    )
                }
            }

            EditorSection(
                title = "Notas",
                description = "Este campo es opcional.",
            ) {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 120.dp),
                    label = {
                        Text("Notas adicionales")
                    },
                    placeholder = {
                        Text(
                            text = if (taskType == TaskType.MEDICATION) {
                                "Ejemplo: Tomar después del desayuno"
                            } else {
                                "Ejemplo: Llevar documentos y estudios"
                            },
                            fontSize = 17.sp,
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default,
                    ),
                    colors = editorTextFieldColors(),
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = accentBackground,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = accentColor.copy(alpha = 0.25f),
                ),
            ) {
                Text(
                    text = "Revisa la información antes de guardar los cambios.",
                    modifier = Modifier.padding(16.dp),
                    color = accentColor,
                    fontSize = 17.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun EditorSection(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Surface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Border),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    color = Navy,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = description,
                    color = TextMuted,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                )
            }

            HorizontalDivider(color = Border)

            content()
        }
    }
}

@Composable
private fun TaskTypeButton(
    text: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedColor,
                contentColor = Surface,
            ),
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(2.dp, Border),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextPrimary,
            ),
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun EditorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    error: String?,
    imeAction: ImeAction,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 56.dp),
        label = {
            Text(label)
        },
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 17.sp,
            )
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { errorMessage ->
            {
                Text(
                    text = errorMessage,
                    fontSize = 15.sp,
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = imeAction,
        ),
        colors = editorTextFieldColors(),
    )
}

@Composable
private fun SelectionField(
    label: String,
    value: String,
    buttonText: String,
    error: String?,
    onClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = value.ifBlank { "Sin seleccionar" },
            color = if (value.isBlank()) TextMuted else TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )

        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = 2.dp,
                color = if (error != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    Navy
                },
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Navy,
            ),
        ) {
            Text(
                text = buttonText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
private fun editorTextFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Navy,
        focusedLabelColor = Navy,
        cursorColor = Navy,
        errorBorderColor = MaterialTheme.colorScheme.error,
        errorLabelColor = MaterialTheme.colorScheme.error,
    )
