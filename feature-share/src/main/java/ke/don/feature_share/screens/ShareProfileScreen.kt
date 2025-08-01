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
package ke.don.feature_share.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_datasource.domain.models.PodiumProfile
import ke.don.core_designsystem.material_theme.components.leaderboard.CircleFramedImage
import ke.don.core_designsystem.material_theme.components.leaderboard.Crown
import ke.don.core_designsystem.material_theme.components.leaderboard.CrownColor
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate
import ke.don.feature_share.components.PolaroidFrame

@Composable
fun ShareProfileScreen(
    modifier: Modifier = Modifier,
    profile: PodiumProfile,
) {
    val comment = when{
        profile.position < 3 -> "The view from the podium is crazy"
        else -> "Beat my score"
    }
    PolaroidFrame(
        title = "Spotlight"
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(32.dp),
        ) {
            val profile = profile
            val crownColor = when (profile.position) {
                1 -> CrownColor.GOLD
                2 -> CrownColor.SILVER
                3 -> CrownColor.BRONZE
                else -> CrownColor.BLACK
            }

            if (crownColor != CrownColor.BLACK) {
                Crown(crown = crownColor)
            }
            CircleFramedImage(
                imageUrl = profile.profileUrl,
                scale = 2f,
                number = profile.position,
                crownColor = crownColor,
            )

            Text(
                text = profile.userName,
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(
                text = "High Score: ${profile.score} \uD83C\uDFC6 ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(8.dp))

            AnimatedArrow()

            Spacer(Modifier.height(16.dp))

            Text(
                text = comment,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        
    }
}

@Composable
fun AnimatedArrow(
    modifier: Modifier = Modifier,
) {
    val swingAngle by rememberInfiniteTransition(label = "swing").animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "swing-angle",
    )

    val arrowColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = Modifier.size(120.dp, 80.dp)) {
        val start = Offset(0f, size.height)
        val end = Offset(size.width, 0f)

        // Control point BELOW the diagonal for inward bend
        val control = Offset(size.width / 2f, size.height * 1.3f)

        val pivot = Offset(size.width / 2f, size.height / 2f)

        rotate(degrees = swingAngle, pivot = pivot) {
            val path = Path().apply {
                moveTo(start.x, start.y)
                quadraticBezierTo(control.x, control.y, end.x, end.y)
            }

            drawPath(
                path = path,
                color = arrowColor,
                style = Stroke(width = 5f, cap = StrokeCap.Round),
            )

            drawCircle(
                color = arrowColor,
                center = end,
                radius = 6f,
            )
        }
    }
}

@Preview
@Composable
fun ShareProfileTemplate(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean,
) {
    val fakeProfile = PodiumProfile(
        id = "user_12345",
        userName = "Donald Isoe",
        profileUrl = "https://i.pravatar.cc/150?img=3", // random avatar
        createdAt = "2024-12-01T12:34:56Z",
        score = 4200,
        position = 1,
        lastPlayed = System.currentTimeMillis() - 86400000L, // 1 day ago
    )

    ThemedPreviewTemplate(isDark) {
        ShareProfileScreen(profile = fakeProfile)
    }
}
