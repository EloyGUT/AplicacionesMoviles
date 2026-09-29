package com.example.medreminders.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/** Stores each signed-in user's reminders in users/{uid}/tasks. */
class TaskRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private fun userTasks() = auth.currentUser?.uid?.let { userId ->
        firestore.collection("users").document(userId).collection("tasks")
    }

    fun observeTasks(
        onTasksChanged: (List<Task>) -> Unit,
        onError: (String) -> Unit,
    ): ListenerRegistration? {
        val tasks = userTasks() ?: run {
            onTasksChanged(emptyList())
            return null
        }

        return tasks.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError("No fue posible cargar los recordatorios.")
                return@addSnapshotListener
            }
            onTasksChanged(snapshot?.documents?.mapNotNull(DocumentSnapshot::toTaskOrNull).orEmpty())
        }
    }

    fun save(task: Task, onComplete: (String?) -> Unit) {
        val tasks = userTasks() ?: return onComplete("Inicia sesión para guardar recordatorios.")
        tasks.document(task.id).set(task.toMap()).addOnCompleteListener { result ->
            onComplete(if (result.isSuccessful) null else "No fue posible guardar el recordatorio.")
        }
    }

    fun delete(taskId: String, onComplete: (String?) -> Unit) {
        val tasks = userTasks() ?: return onComplete("Inicia sesión para eliminar recordatorios.")
        tasks.document(taskId).delete().addOnCompleteListener { result ->
            onComplete(if (result.isSuccessful) null else "No fue posible eliminar el recordatorio.")
        }
    }
}

private fun Task.toMap(): Map<String, Any?> = mapOf(
    "type" to type.name,
    "name" to name,
    "timeDisplay" to timeDisplay,
    "dateDisplay" to dateDisplay,
    "dose" to dose,
    "alarmEnabled" to alarmEnabled,
    "notes" to notes,
)

private fun DocumentSnapshot.toTaskOrNull(): Task? {
    val type = getString("type")?.let { value ->
        runCatching { TaskType.valueOf(value) }.getOrNull()
    } ?: return null
    val name = getString("name") ?: return null
    val timeDisplay = getString("timeDisplay") ?: return null
    val dateDisplay = getString("dateDisplay") ?: return null

    return Task(
        id = id,
        type = type,
        name = name,
        timeDisplay = timeDisplay,
        dateDisplay = dateDisplay,
        dose = getString("dose"),
        alarmEnabled = getBoolean("alarmEnabled") ?: true,
        notes = getString("notes"),
    )
}
