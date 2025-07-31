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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_datasource.domain.models.ChatMessage
import ke.don.core_datasource.domain.models.SpotlightModel
import ke.don.core_datasource.domain.models.SpotlightPair
import ke.don.core_designsystem.material_theme.components.AvatarImage
import ke.don.core_designsystem.material_theme.components.TextBubble
import ke.don.core_designsystem.material_theme.components.toRelativeTime
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate
import ke.don.feature_share.components.PolaroidFrame

@Composable
fun ShareSpotlightScreen(
    modifier: Modifier = Modifier,
    spotlightModel: SpotlightModel,
) {
    PolaroidFrame(
        title = "Spotlight"
    ){
        SpotlightComponent(
            modifier = modifier,
            spotlightModel = spotlightModel,
        )
    }
}

@Composable
fun SpotlightComponent(
    modifier: Modifier = Modifier,
    spotlightModel: SpotlightModel,
) {
    val spotlightPair = spotlightModel.spotlightPair ?: return

    Column(
        modifier = modifier
            .padding(16.dp)
            .animateContentSize(), // smoother transition
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AvatarImage(
            profileUrl = spotlightModel.profileUrl,
            size = 56.dp,
        )

        Text(
            text = buildAnnotatedString {
                if (spotlightPair.isHighScore) {
                    append("🏆 New high score! ${spotlightPair.score} pts")
                } else {
                    append("Score: ${spotlightPair.score}")
                }
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        // Bot prompt
        TextBubble(
            isSent = false,
            text = "What beats ${spotlightPair.prompt}",
            isError = false,
            pointsEarned = spotlightPair.botMessage.awardedPoints,
            timestamp = spotlightPair.botMessage.timestamp.toRelativeTime(),
        )

        // User response
        TextBubble(
            isSent = true,
            text = spotlightPair.userMessage.answer,
            isError = false,
            timestamp = spotlightPair.userMessage.timestamp.toRelativeTime(),
            profileUrl = spotlightModel.profileUrl,
        )

        // Bot feedback
        TextBubble(
            isSent = false,
            text = spotlightPair.botMessage.message,
            isError = false,
            pointsEarned = spotlightPair.botMessage.awardedPoints,
            timestamp = spotlightPair.botMessage.timestamp.toRelativeTime(),
        )
    }
}

@Preview
@Composable
fun ShareSpotlightScreenPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean,
) {
    val spotlightPair = SpotlightPair(
        userMessage = ChatMessage.User(
            answer = "paper",
            timestamp = System.currentTimeMillis() - 60_000,
        ),
        botMessage = ChatMessage.Bot(
            message = "Nice one! You beat rock.",
            timestamp = System.currentTimeMillis(),
            awardedPoints = 10,
        ),
        prompt = "rock",
        score = 30,
        isHighScore = true,
    )

    val spotlightModel = SpotlightModel(
        spotlightPair = spotlightPair,
        profileUrl = null, // or provide a fake URL if needed
    )

    ThemedPreviewTemplate(isDark) {
        ShareSpotlightScreen(spotlightModel = spotlightModel)
    }
}
