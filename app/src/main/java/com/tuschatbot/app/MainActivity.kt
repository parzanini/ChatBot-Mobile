package com.tuschatbot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.compose.TUSChatbotTheme
import com.tuschatbot.app.components.ChatTopBar
import com.tuschatbot.app.screens.HomeScreen
import com.tuschatbot.app.screens.HelpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Show app behind system bars
        setContent {
            // Apply the app theme colors
            TUSChatbotTheme(darkTheme = false, dynamicColor = false) {
                // Create a variable to track if Help screen should be shown
                val showHelp = remember { mutableStateOf(false) }

                // Basic layout structure with top bar and content area
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        // Top bar with help and back buttons
                        ChatTopBar(
                            onHelpClick = { showHelp.value = true }, // When ? is clicked, show Help
                            onBackClick = { showHelp.value = false }, // When <- is clicked, show Home
                            showBackButton = showHelp.value // Show back button only on Help screen
                        )
                    }
                ) { innerPadding ->
                    // Display Help screen if showHelp is true, otherwise show Home screen
                    if (showHelp.value) {
                        HelpScreen(
                            modifier = Modifier.padding(innerPadding),
                            onBack = { showHelp.value = false } // Go back to Home when needed
                        )
                    } else {
                        HomeScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}
