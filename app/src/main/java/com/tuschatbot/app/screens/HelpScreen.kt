package com.tuschatbot.app.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun HelpScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onExitApp: () -> Unit = {}
) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // About the App
            Text(
                text = "About This App",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "This chatbot assistant provides information about TUS (Technological University of the Shannon). " +
                        "Ask questions about courses, facilities, campus information, and more!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // How to Use
            Text(
                text = "How to Use",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "1. Type your question in the text box at the bottom\n" +
                        "2. Press the send button\n" +
                        "3. Wait for the bot to respond\n" +
                        "4. Click on sources to view more details (if available)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Limitations
            Text(
                text = "Important Note",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "This app only contains information that was scraped from the TUS website. " +
                        "If you cannot find what you're looking for, please contact TUS directly.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // TUS Contact Information
            Text(
                text = "TUS Contact Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ContactLink(
                label = "Website:",
                value = "www.tus.ie",
                url = "https://www.tus.ie"
            )

            ContactLink(
                label = "Email:",
                value = "info@tus.ie",
                url = "mailto:info@tus.ie"
            )

            ContactLink(
                label = "Phone:",
                value = "+353 61 293000",
                url = "tel:+35361293000"
            )

            // Developer Information
            Text(
                text = "Developer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            Text(
                text = "Developed by: Thiago Gomes Parzanini",
                style = MaterialTheme.typography.bodyMedium,

            )
            ContactLink(
                label = "Contact:",
                value = "K00287912@student.tus.ie",
                url = "mailto:K00287912@student.tus.ie"
            )

            Button(
                onClick = onExitApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text("EXIT APP")
            }
        }
    }

@Composable
private fun ContactLink(
    label: String,
    value: String,
    url: String
) {
    val context = LocalContext.current

    Row(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        )
    }
}

