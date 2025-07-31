/*
 * Copyright © 2025 Donald O. Isoe (isoedonald@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ke.don.core_designsystem.material_theme.components.Images
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate

@Composable
fun CircleFramedImage(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    crownColor: CrownColor,
    number: Int = crownColor.rank(),
    borderWidth: Dp = 2.dp,
    scale: Float = crownColor.scale(),
) {
    val size = 72.dp * scale
    val scaledModifier = modifier.size(size)
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = scaledModifier,
    ) {
        // Circular image with border
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false) // <-- Important for saving/capturing
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(Images.appLogo),
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(borderWidth, crownColor.toColor(), CircleShape),
        )

        // Number badge
        Box(
            modifier = Modifier
                .offset(y = 10.dp)
                .background(crownColor.toColor(), shape = CircleShape)
                .padding(
                    horizontal = 8.dp,
                    vertical = 2.dp,
                ),
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                ),
            )
        }
    }
}

@Preview
@Composable
fun CircleFramedImagePreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean,
) {
    ThemedPreviewTemplate(isDark) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Crown(
                    crown = CrownColor.SILVER,
                )

                CircleFramedImage(
                    number = 2,
                    crownColor = CrownColor.SILVER,
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Crown(crown = CrownColor.GOLD)

                CircleFramedImage(
                    number = 1,
                    crownColor = CrownColor.GOLD,
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Crown(crown = CrownColor.BRONZE)

                CircleFramedImage(
                    number = 3,
                    crownColor = CrownColor.BRONZE,
                )
            }
        }
    }
}
