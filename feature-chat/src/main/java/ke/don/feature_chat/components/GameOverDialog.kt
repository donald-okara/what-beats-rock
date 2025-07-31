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
package ke.don.feature_chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ke.don.core_datasource.domain.models.SpotlightModel
import ke.don.core_designsystem.R
import ke.don.core_designsystem.material_theme.components.DialogType
import ke.don.core_designsystem.material_theme.components.Images
import ke.don.core_designsystem.material_theme.components.TextBubble
import ke.don.core_designsystem.material_theme.components.toRelativeTime
import ke.don.feature_chat.models.ChatIntentHandler
import ke.don.feature_chat.models.ChatUiState

@Composable
fun SpotlightComponent(
    modifier: Modifier = Modifier,
    spotlightModel: SpotlightModel,
    navigateToShare: (SpotlightModel) -> Unit,
) {
    val spotlightPair = spotlightModel.spotlightPair

    AnimatedVisibility(
        visible = spotlightPair != null,
    ) {
        Column(
            modifier = modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = if (spotlightPair?.isHighScore == true) "New high score! \uD83C\uDFC6 ${spotlightPair?.score} points" else "Score: ${spotlightPair?.score}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            TextBubble(
                isSent = false,
                text = "What beats ${spotlightPair?.prompt}",
                isError = false,
                pointsEarned = spotlightPair?.botMessage?.awardedPoints,
                timestamp = spotlightPair?.botMessage?.timestamp?.toRelativeTime(),
            )

            TextBubble(
                isSent = true,
                text = spotlightPair?.userMessage?.answer ?: "",
                isError = false,
                timestamp = spotlightPair?.userMessage?.timestamp?.toRelativeTime(),
                profileUrl = spotlightModel.profileUrl,
            )

            TextBubble(
                isSent = false,
                text = spotlightPair?.botMessage?.message ?: "",
                isError = false,
                pointsEarned = spotlightPair?.botMessage?.awardedPoints,
                timestamp = spotlightPair?.botMessage?.timestamp?.toRelativeTime(),
            )

            Button(onClick = { navigateToShare(spotlightModel) }) {
                Text(text = "Share")
            }
        }
    }
}

@Composable
fun GameOverDialog(
    modifier: Modifier = Modifier,
    uiState: ChatUiState,
    handleIntent: (ChatIntentHandler) -> Unit,
    dialogType: DialogType = DialogType.NEUTRAL,
    enabled: Boolean = true,
    navigateToShare: (SpotlightModel) -> Unit,
) {
    val onContainerColor = when (dialogType) {
        DialogType.WARNING -> MaterialTheme.colorScheme.primary
        DialogType.DANGER -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }

    val disabledColor = onContainerColor.copy(alpha = 0.6f)

    val spotlightModel = SpotlightModel(
        profileUrl = uiState.profile.photoUrl,
        spotlightPair = uiState.spotlightPair,
    )

    AlertDialog(
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Image(
                painter = painterResource(Images.appLogo),
                contentDescription = "Game over",
                Modifier.size(32.dp),
            )
        },
        title = { Text(text = "Game over") },
        text = {
            SpotlightComponent(modifier, spotlightModel, navigateToShare)
        },
        onDismissRequest = { handleIntent(ChatIntentHandler.ToggleGameOverDialog) },
        confirmButton = {
            TextButton(onClick = { handleIntent(ChatIntentHandler.ResetState) }, enabled = enabled) {
                Text(
                    text = "New Game",
                    color = if (enabled) onContainerColor else disabledColor,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { handleIntent(ChatIntentHandler.ToggleGameOverDialog) }) {
                Text(
                    text = stringResource(R.string.dismiss),
                    color = onContainerColor,
                )
            }
        },
        modifier = modifier,
    )
}
