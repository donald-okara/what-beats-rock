package ke.don.core_designsystem.material_theme.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate

@Composable
fun Modifier.shimmerBackground(
    shape: Shape = RectangleShape,
    shimmerColors: List<Color> = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ),
    durationMillis: Int = 1200,
    shimmerWidth: Float = 500f
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -shimmerWidth,
        targetValue = shimmerWidth * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerAnim"
    )

    val brush = Brush.horizontalGradient(
        colors = shimmerColors,
        startX = translateAnim,
        endX = translateAnim + shimmerWidth
    )

    this.background(brush = brush, shape = shape)
}


@Preview
@Composable
fun ShimmerPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean
){
    ThemedPreviewTemplate(isDark) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .shimmerBackground(CircleShape)
            )

            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .shimmerBackground(shape = RoundedCornerShape(12.dp))
            )
        }

    }
}