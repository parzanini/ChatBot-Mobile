package com.tuschatbot.app.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.tuschatbot.app.ui.theme.TUSChatbotTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Chatbot",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF197F88)
        )
    )
}



