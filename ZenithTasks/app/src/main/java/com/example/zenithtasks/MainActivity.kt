package com.example.zenithtasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image // Import for Image Composable
import androidx.compose.foundation.layout.Box // Import for Box Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale // Import for ContentScale
import androidx.compose.ui.res.painterResource // Import for painterResource
import com.example.zenithtasks.Navigation.AppNavigation
import com.example.zenithtasks.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import com.example.zenithtasks.ui.ui.themes.ZenithTasksTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZenithTasksTheme {
                // We will replace Surface with a Box that contains the image and then the content
                Box(modifier = Modifier.fillMaxSize()) { // Use Box as the main container
                    Image(
                        painter = painterResource(id = R.drawable.zenith_task_bg), // REPLACE 'your_wallpaper_name' with your image file name
                        contentDescription = "Background wallpaper",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // Scales the image to fill the bounds, cropping if necessary
                    )

                    // Your existing Surface for content goes on top of the image
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        // IMPORTANT: Make the Surface background transparent or semi-transparent
                        // so the wallpaper can show through.
                        color = Color.Transparent
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}