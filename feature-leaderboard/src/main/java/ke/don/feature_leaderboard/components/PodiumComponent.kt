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
package ke.don.feature_leaderboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_datasource.domain.models.PodiumProfile
import ke.don.core_designsystem.material_theme.components.leaderboard.CircleFramedImage
import ke.don.core_designsystem.material_theme.components.leaderboard.Crown
import ke.don.core_designsystem.material_theme.components.leaderboard.CrownColor
import ke.don.core_designsystem.material_theme.components.shimmerBackground
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate
import ke.don.feature_leaderboard.models.LeaderboardIntentHandler

@Composable
fun PodiumTopThree(
    profiles: List<PodiumProfile>,
    modifier: Modifier = Modifier,
    handleIntent: (LeaderboardIntentHandler) -> Unit,
) {
    // Get specific podium slots if present
    val first = profiles.firstOrNull { it.position == 1 }
    val second = profiles.firstOrNull { it.position == 2 }
    val third = profiles.firstOrNull { it.position == 3 }

    // Build the row in correct order
    val podiumSlots = listOfNotNull(
        second?.let { it to CrownColor.SILVER },
        first?.let { it to CrownColor.GOLD },
        third?.let { it to CrownColor.BRONZE },
    )

    Row(
        modifier = modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ) {
        podiumSlots.forEach { (profile, crownColor) ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomCenter,
            ) {
                PodiumItem(
                    profile = profile,
                    crownColor = crownColor,
                    onClick = { handleIntent(LeaderboardIntentHandler.NavigateToProfile(it)) },
                )
            }
        }
    }
}

@Composable
fun PodiumItem(
    modifier: Modifier = Modifier,
    profile: PodiumProfile,
    crownColor: CrownColor,
    onClick: (String) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.clickable(onClick = { onClick(profile.id) }),
    ) {
        Crown(
            crown = crownColor,
        )

        CircleFramedImage(
            imageUrl = profile.profileUrl,
            crownColor = crownColor,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = profile.userName,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = profile.score.toString() + " ⭐",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun PodiumTopThreeShimmer(
    modifier: Modifier = Modifier,
) {
    val shimmerScales = listOf(
        0.9f, // 2nd (left)
        1.0f, // 1st (center)
        0.8f, // 3rd (right)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        shimmerScales.forEach { scale ->
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter,
            ) {
                PodiumItemShimmer(scale = scale)
            }
        }
    }
}

@Composable
fun PodiumItemShimmer(scale: Float = 1f) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp),
    ) {
        // Profile picture placeholder
        Box(
            modifier = Modifier
                .size(72.dp * scale)
                .shimmerBackground(CircleShape),
        )

        // Name placeholder
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .height(16.dp)
                .width(60.dp * scale)
                .shimmerBackground(MaterialTheme.shapes.medium),
        )

        // Score placeholder
        Box(
            modifier = Modifier
                .height(14.dp)
                .width(40.dp * scale)
                .shimmerBackground(MaterialTheme.shapes.medium),
        )
    }
}

@Preview
@Composable
fun PodiumTopThreePreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean,
) {
    val sampleProfiles = listOf(
        PodiumProfile(
            position = 1,
            score = 95,
            profileUrl = "https://example.com/first.jpg",
            userName = "John Doe",
            id = "",
        ),
        PodiumProfile(
            position = 2,
            score = 85,
            profileUrl = "https://example.com/second.jpg",
            userName = "Jane Smith",
            id = "",
        ),
        PodiumProfile(
            position = 3,
            score = 75,
            profileUrl = "https://example.com/third.jpg",
            userName = "Bob Johnson",
            id = "",
        ),
    )

    ThemedPreviewTemplate(isDark) {
        PodiumTopThree(
            profiles = sampleProfiles,
            handleIntent = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@Preview
@Composable
fun PodiumTopThreeShimmerPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean,
) {
    ThemedPreviewTemplate(isDark) {
        PodiumTopThreeShimmer()
    }
}
