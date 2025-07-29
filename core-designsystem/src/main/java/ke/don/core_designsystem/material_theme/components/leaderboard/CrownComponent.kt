package ke.don.core_designsystem.material_theme.components.leaderboard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate

@Composable
fun Crown(
    modifier: Modifier = Modifier,
    crown: CrownColor,
    scale: Float = crown.scale(),
) {
    val crownHeight = when (crown) {
        CrownColor.BRONZE -> 24.dp
        CrownColor.SILVER -> 36.dp
        CrownColor.GOLD -> 48.dp
        CrownColor.BLACK -> 64.dp
    } * scale

    val crownWidth = 40.dp * scale
    val crownColor = crown.toColor()

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f * scale,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "yOffset"
    )

    Box(
        modifier = modifier
            .offset(y = yOffset.dp)
            .size(width = crownWidth, height = crownHeight)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val path = Path().apply {
                moveTo(0f, height)
                lineTo(width * 0.2f, height * 0.4f)
                lineTo(width * 0.4f, height)

                lineTo(width * 0.5f, height * 0.2f) // tallest center peak
                lineTo(width * 0.6f, height)

                lineTo(width * 0.8f, height * 0.5f)
                lineTo(width, height)
                close()
            }

            drawPath(path, color = crownColor)
        }
    }
}


@Preview
@Composable
fun CrownPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean
) {
    ThemedPreviewTemplate(isDark) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Crown(crown = CrownColor.SILVER)
                Text("2nd", fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Crown(crown = CrownColor.GOLD)
                Text("1st", fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Crown(crown = CrownColor.BRONZE)
                Text("3rd", fontSize = 12.sp)
            }
        }
    }

}

