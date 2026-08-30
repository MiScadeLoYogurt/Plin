package com.plin.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Bottom inset for main/settings FABs — keep in sync across screens. */
internal val PlinFabBottomPadding = 40.dp

@Composable
fun SettingsScaffold(
    title: String,
    onHome: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 48.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onHome) {
                    HomeOutlineIcon(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
            content()
        }

        FloatingActionButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(start = 20.dp, end = 20.dp, bottom = PlinFabBottomPadding),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
        ) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}

/** Simple house outline in the theme primary color (teal line art). */
@Composable
private fun HomeOutlineIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.09f
        val stroke = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        val left = size.width * 0.18f
        val right = size.width * 0.82f
        val roofPeak = Offset(size.width * 0.5f, size.height * 0.12f)
        val eavesY = size.height * 0.42f
        val floorY = size.height * 0.88f

        val roof = Path().apply {
            moveTo(left, eavesY)
            lineTo(roofPeak.x, roofPeak.y)
            lineTo(right, eavesY)
        }
        drawPath(path = roof, color = color, style = stroke)

        val body = Path().apply {
            moveTo(left, eavesY)
            lineTo(left, floorY)
            lineTo(right, floorY)
            lineTo(right, eavesY)
        }
        drawPath(path = body, color = color, style = stroke)

        val doorLeft = size.width * 0.42f
        val doorRight = size.width * 0.58f
        val doorTop = size.height * 0.58f
        val door = Path().apply {
            moveTo(doorLeft, floorY)
            lineTo(doorLeft, doorTop)
            lineTo(doorRight, doorTop)
            lineTo(doorRight, floorY)
        }
        drawPath(path = door, color = color, style = stroke)
    }
}
