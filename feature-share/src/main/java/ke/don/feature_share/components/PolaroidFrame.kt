package ke.don.feature_share.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PolaroidFrame(
    modifier: Modifier = Modifier,
    title: String? = null,
    tiltDegrees: Float = (-3).toFloat(), // slight random tilt
    patternColor: Color = Color(0xFFF0F0F0),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                rotationZ = tiltDegrees
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Patterned border background
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(patternColor, Color.White, patternColor),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(min = 200.dp)
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = content
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!title.isNullOrBlank()) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

