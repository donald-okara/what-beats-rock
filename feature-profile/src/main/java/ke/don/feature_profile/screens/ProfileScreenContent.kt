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
package ke.don.feature_profile.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_datasource.domain.models.PodiumProfile
import ke.don.core_designsystem.material_theme.components.ConfirmationDialog
import ke.don.core_designsystem.material_theme.components.ConfirmationDialogWithChecklist
import ke.don.core_designsystem.material_theme.components.DialogType
import ke.don.core_designsystem.material_theme.components.leaderboard.CircleFramedImage
import ke.don.core_designsystem.material_theme.components.leaderboard.Crown
import ke.don.core_designsystem.material_theme.components.leaderboard.CrownColor
import ke.don.core_designsystem.material_theme.components.shimmerBackground
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate
import ke.don.feature_profile.R
import ke.don.feature_profile.components.ProfileBottomSheet
import ke.don.feature_profile.model.ProfileIntentHandler
import ke.don.feature_profile.model.ProfileUiState
import java.text.DateFormat
import java.util.Date

@Composable
fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState,
    navigateToShare: () -> Unit,
    navigateToSignin: () -> Unit,
    intentHandler: (ProfileIntentHandler) -> Unit,
) {
    val profile = uiState.profile
    val isLoading = uiState.isLoading
    val crownColor = when (profile.position) {
        1 -> CrownColor.GOLD
        2 -> CrownColor.SILVER
        3 -> CrownColor.BRONZE
        else -> CrownColor.BLACK
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .shimmerBackground(shape = CircleShape),
            )
            Box(
                modifier = Modifier
                    .height(28.dp)
                    .width(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerBackground(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            repeat(2) {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(220.dp)
                        .clip(RoundedCornerShape(50))
                        .shimmerBackground(),
                )
            }
        } else {
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

            Spacer(modifier = Modifier.height(16.dp))

            profile.lastPlayed?.let {
                val formatted = DateFormat.getDateInstance().format(Date(it))
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.last_played, formatted)) },
                    leadingIcon = { Icon(Icons.Default.History, contentDescription = null) },
                )
            }

            profile.createdAt?.let {
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.joined, it.take(10))) },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                )
            }

            profile.score.let {
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.high_score, it)) },
                    leadingIcon = { Icon(Icons.Outlined.Star, contentDescription = null) },
                )
            }
        }
    }

    if (!isLoading) {
        ProfileBottomSheet(
            modifier = modifier,
            state = uiState,
            intentHandler = intentHandler,
            navigateToShare = navigateToShare,
        )
    }

    if (uiState.showDeleteDialog) {
        ConfirmationDialogWithChecklist(
            onDismissRequest = { intentHandler(ProfileIntentHandler.ToggleDeleteDialog) },
            onConfirmation = {
                intentHandler(ProfileIntentHandler.DeleteProfile(navigateToSignin))
            },
            dialogTitle = stringResource(R.string.delete_profile),
            dialogText = stringResource(R.string.delete_profile_confirmation),
            dialogType = DialogType.DANGER,
            icon = Icons.Outlined.PersonOff,
            checklistItems = listOf(
                stringResource(R.string.this_would_delete_all_your_progress),
                stringResource(R.string.this_cannot_be_undone),
            ),
        )
    }

    if (uiState.showSignOutDialog) {
        ConfirmationDialog(
            modifier = modifier,
            onDismissRequest = { intentHandler(ProfileIntentHandler.ToggleSignOutDialog) },
            onConfirmation = { intentHandler(ProfileIntentHandler.SignOut(navigateToSignin)) },
            dialogTitle = stringResource(R.string.sign_out),
            dialogText = stringResource(R.string.are_you_sure_you_want_to_sign_out),
            dialogType = DialogType.DANGER,
            icon = Icons.Default.Logout,
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean,
) {
    val fakeProfile = PodiumProfile(
        id = "user_12345",
        userName = "Donald Isoe",
        profileUrl = "https://i.pravatar.cc/150?img=3", // random avatar
        createdAt = "2024-12-01T12:34:56Z",
        score = 4200,
        lastPlayed = System.currentTimeMillis() - 86400000L, // 1 day ago
    )

    ThemedPreviewTemplate(isDark) {
        ProfileScreenContent(
            uiState = ProfileUiState(
                profile = fakeProfile,
                isLoading = false,
            ),
            intentHandler = {},
            navigateToSignin = {},
            navigateToShare = {},
        )
    }
}
