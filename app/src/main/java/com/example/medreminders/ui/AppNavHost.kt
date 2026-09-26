package com.example.medreminders.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medreminders.ui.screens.DetailScreen
import com.example.medreminders.ui.screens.HomeScreen
import com.example.medreminders.ui.screens.NewTaskScreen

sealed class Screen(
    val route: String
) {

    object Home : Screen("home")

    object NewTask : Screen("new_task")

    object Detail : Screen("detail/{taskId}") {

        fun createRoute(taskId: String): String {
            return "detail/$taskId"
        }
    }
}

@Composable
fun AppNavHost(
    viewModel: TaskViewModel
) {

    val navController = rememberNavController()

    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {

            HomeScreen(
                tasks = uiState.tasks,
                toast = uiState.toastMessage,

                onClearToast = viewModel::clearToast,

                onSelectTask = { task ->
                    navController.navigate(
                        Screen.Detail.createRoute(task.id)
                    )
                },

                onNewTask = {
                    navController.navigate(
                        Screen.NewTask.route
                    )
                },
            )
        }

        composable(Screen.NewTask.route) {

            NewTaskScreen(

                onBack = {
                    navController.popBackStack()
                },

                onSave = { task ->

                    viewModel.addTask(task)

                    navController.popBackStack()
                },
            )
        }

        composable(
            route = Screen.Detail.route,

            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                }
            ),
        ) { backStackEntry ->

            val taskId =
                backStackEntry.arguments?.getString("taskId")
                    ?: return@composable

            val task =
                uiState.tasks.find {
                    it.id == taskId
                } ?: return@composable

            DetailScreen(

                task = task,

                onBack = {
                    navController.popBackStack()
                },

                onDelete = { id ->

                    viewModel.deleteTask(id)

                    navController.popBackStack()
                },

                onEdit = {
                    // Navegar a pantalla de edición
                },
            )
        }
    }
}