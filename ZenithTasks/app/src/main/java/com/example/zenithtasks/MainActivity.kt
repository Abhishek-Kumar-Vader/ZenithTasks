package com.example.zenithtasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.zenithtasks.Navigation.AppNavigation
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme
import com.example.zenithtasks.viewmodel.TaskViewModel
import com.example.zenithtasks.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    private val zenithTasksApplication by lazy { application as ZenithTasksApplication }

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(zenithTasksApplication.repository)
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "MainActivity onCreate called.")
        Toast.makeText(this, "MainActivity Started!", Toast.LENGTH_SHORT).show()
        //enableEdgeToEdge()
        setContent {
            ZenithTasksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    // Use LocalContext for toasts inside Composable
                    val context = LocalContext.current
                    Toast.makeText(context, "Compose UI Loaded!", Toast.LENGTH_SHORT).show()
                    AppNavigation(navController = navController, taskViewModel = taskViewModel)
                }
            }
        }
    }
}