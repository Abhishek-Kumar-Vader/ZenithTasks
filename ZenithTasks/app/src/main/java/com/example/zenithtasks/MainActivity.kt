package com.example.zenithtasks

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zenithtasks.Navigation.AppNavGraph
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme
import com.example.zenithtasks.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue // NEW IMPORT

@AndroidEntryPoint // Mark MainActivity for Hilt injection
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request POST_NOTIFICATIONS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel() // Get ThemeViewModel
            val darkThemeEnabled by themeViewModel.darkThemeEnabled.collectAsState() // Observe theme state
            ZenithTasksTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Just call your AppNavGraph composable
                    AppNavGraph()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ZenithTasksTheme {
        // You can't easily preview the entire NavHost structure here without complex mocks.
        // This preview is just for the MainActivity's content itself.
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Text("ZenithTasks App")
        }
    }
}