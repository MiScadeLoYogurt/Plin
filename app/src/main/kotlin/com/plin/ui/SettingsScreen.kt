package com.plin.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onHome: () -> Unit,
    onBack: () -> Unit,
    onOpenFriendOfTheWeek: () -> Unit,
) {
    SettingsScaffold(
        title = "Settings",
        onHome = onHome,
        onBack = onBack,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = onOpenFriendOfTheWeek,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Friend of the Week",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
