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
package ke.don.feature_profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ke.don.core_datasource.domain.models.PodiumProfile
import ke.don.core_designsystem.material_theme.components.BottomSheetItem
import ke.don.core_designsystem.material_theme.components.BottomSheetItemData
import ke.don.feature_profile.R
import ke.don.feature_profile.model.ProfileIntentHandler
import ke.don.feature_profile.model.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    modifier: Modifier = Modifier,
    state: ProfileUiState,
    intentHandler: (ProfileIntentHandler) -> Unit,
    navigateToShare: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    val actions = buildList {
        add(
            BottomSheetItemData(
                icon = Icons.Outlined.Share,
                text = stringResource(R.string.share),
                onClick = { navigateToShare() },
            ),
        )
        if (state.isMyProfile) {
            add(
                BottomSheetItemData(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    text = stringResource(R.string.logout),
                    onClick = {
                        intentHandler(ProfileIntentHandler.ToggleSignOutDialog)
                    },
                ),
            )
            add(
                BottomSheetItemData(
                    icon = Icons.Outlined.PersonOff,
                    text = stringResource(R.string.delete_profile),
                    onClick = {
                        intentHandler(ProfileIntentHandler.ToggleDeleteDialog)
                    },
                ),
            )
        }
    }

    if (state.showSheet) {
        ModalBottomSheet(
            modifier = modifier.padding(8.dp),
            onDismissRequest = {
                intentHandler(ProfileIntentHandler.ToggleBottomSheet)
            },
            sheetState = sheetState,
        ) {
            LazyColumn(
                modifier = modifier.padding(16.dp),
            ) {
                stickyHeader {
                    ProfileSheetHeader(
                        modifier = modifier,
                        profile = state.profile,
                    )
                }
                items(actions.size) { index ->
                    BottomSheetItem(
                        modifier = modifier,
                        icon = actions[index].icon,
                        text = actions[index].text,
                        onClick = actions[index].onClick,
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileSheetHeader(
    modifier: Modifier = Modifier,
    profile: PodiumProfile,
) {
    val imageSize = 80.dp

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            AsyncImage(
                model = profile.profileUrl,
                contentDescription = stringResource(R.string.profile_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape),

            )
            Spacer(modifier = modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = modifier.weight(1f),
            ) {
                Text(
                    text = profile.userName,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                )
            }
        }
        Spacer(modifier = modifier.height(8.dp))

        HorizontalDivider()

        Spacer(modifier = modifier.height(8.dp))
    }
}
