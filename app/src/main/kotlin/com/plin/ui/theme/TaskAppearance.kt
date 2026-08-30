package com.plin.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plin.domain.enums.TaskCategory

/**
 * Shared look for task rows: rounded rectangle container.
 * Color comes from [colorForCategory]; shape/padding are fixed here.
 */
object TaskAppearance {
    val cornerRadius: Dp = 12.dp
    val borderWidth: Dp = 2.dp
    val contentPaddingHorizontal: Dp = 14.dp
    val contentPaddingVertical: Dp = 12.dp
    val shape = RoundedCornerShape(cornerRadius)

    fun colorForCategory(category: TaskCategory): Color = when (category) {
        TaskCategory.GENERIC -> Color(0xA6A6A6)
        TaskCategory.FUN -> Color(0xD253D4)
        TaskCategory.PERSONAL_DEVELOPMENT -> Color(0x7E34BE)
        TaskCategory.SOCIAL_ACTIVITY -> Color(0x18C09E)
        TaskCategory.CHORE -> Color(0xE3912D)
        TaskCategory.ADULTING -> Color(0x2EB516)
        TaskCategory.SPORT -> Color(0x1437D5)
        TaskCategory.HEALTH -> Color(0xCF3A4B)
        TaskCategory.SPECIAL -> Color(0x12F9E6)
        TaskCategory.FRIEND_OF_THE_WEEK -> Color(0xF5A623)
    }

    fun backgroundForCategory(category: TaskCategory): Color =
        colorForCategory(category).copy(alpha = 0.12f)
}
