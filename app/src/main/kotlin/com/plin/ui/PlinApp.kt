package com.plin.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private enum class PlinScreen {
    TASK_LIST,
    SETTINGS,
    FRIEND_OF_THE_WEEK_SETTINGS,
}

@Composable
fun PlinApp() {
    var screen by remember { mutableStateOf(PlinScreen.TASK_LIST) }

    when (screen) {
        PlinScreen.TASK_LIST -> TaskListScreen(
            onOpenSettings = { screen = PlinScreen.SETTINGS },
        )
        PlinScreen.SETTINGS -> SettingsScreen(
            onHome = { screen = PlinScreen.TASK_LIST },
            onBack = { screen = PlinScreen.TASK_LIST },
            onOpenFriendOfTheWeek = { screen = PlinScreen.FRIEND_OF_THE_WEEK_SETTINGS },
        )
        PlinScreen.FRIEND_OF_THE_WEEK_SETTINGS -> FriendOfTheWeekSettingsScreen(
            onHome = { screen = PlinScreen.TASK_LIST },
            onBack = { screen = PlinScreen.SETTINGS },
        )
    }
}
