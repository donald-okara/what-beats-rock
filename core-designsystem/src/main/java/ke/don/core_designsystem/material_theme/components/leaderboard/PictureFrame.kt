package ke.don.core_designsystem.material_theme.components.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate

@Composable
fun CircleFramedImage(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    number: Int,
    crownColor: CrownColor,
    borderWidth: Dp = 2.dp,
    scale: Float = 1f,
) {
    val scaledModifier = modifier.size(72.dp * scale)

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = scaledModifier
    ) {
        // Circular image with border
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(borderWidth * scale, crownColor.toColor(), CircleShape)
        )

        // Number badge
        Box(
            modifier = Modifier
                .offset(y = 10.dp * scale)
                .background(crownColor.toColor(), shape = CircleShape)
                .padding(
                    horizontal = 8.dp * scale,
                    vertical = 2.dp * scale
                )
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize * scale
                )
            )
        }
    }
}


@Preview
@Composable
fun CircleFramedImagePreview(
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val scale = 1f
                Crown(
                    scale = scale,
                    crown = CrownColor.SILVER
                )

                CircleFramedImage(
                    scale = scale,
                    number = 2,
                    crownColor = CrownColor.SILVER
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val scale = 1.2f

                Crown(scale = scale, crown = CrownColor.GOLD)

                CircleFramedImage(
                    scale = scale,
                    number = 1,
                    crownColor = CrownColor.GOLD
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val scale = 0.8f

                Crown(scale = scale, crown = CrownColor.BRONZE)

                CircleFramedImage(
                    scale = scale,
                    number = 3,
                    crownColor = CrownColor.BRONZE
                )
            }
        }
    }

}
