package com.example.medreminders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medreminders.ui.AppNavHost
import com.example.medreminders.ui.TaskViewModel
import com.example.medreminders.ui.theme.MedRemindersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedRemindersTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: TaskViewModel = viewModel()
                    AppNavHost(viewModel = viewModel)
                }
            }
        }
    }
}
