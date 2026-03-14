package com.tuschatbot.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    onHelpClick: () -> Unit = {}, // Callback when ? button is clicked
    onBackClick: () -> Unit = {}, // Callback when <- button is clicked
    showBackButton: Boolean = false, // Flag to show back button instead of help button
    showHelpButton: Boolean = true
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Chatbot",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        // Show back button on left side if showBackButton is true
        navigationIcon = if (showBackButton) {
            {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        } else {
            {} // Empty button area if not showing back button
        },
        // Show help button on right side only when NOT showing back button
        actions = {
            if (showHelpButton && !showBackButton) {
                IconButton(onClick = onHelpClick) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Help",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        // Apply primary color to top bar background
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}



