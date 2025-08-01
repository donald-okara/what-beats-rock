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
package ke.don.feature_leaderboard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate
import ke.don.feature_leaderboard.components.LeaderboardItem
import ke.don.feature_leaderboard.components.LeaderboardItemShimmer
import ke.don.feature_leaderboard.components.PodiumTopThree
import ke.don.feature_leaderboard.components.PodiumTopThreeShimmer
import ke.don.feature_leaderboard.models.LeaderboardData
import ke.don.feature_leaderboard.models.LeaderboardIntentHandler
import ke.don.feature_leaderboard.models.LeaderboardTemplateProvider
import ke.don.feature_leaderboard.models.LeaderboardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreenContent(
    modifier: Modifier = Modifier,
    uiState: LeaderboardUiState,
    handleIntent: (LeaderboardIntentHandler) -> Unit,
) {
    if (uiState.isLoading) {
        LazyColumn {
            item {
                PodiumTopThreeShimmer()
            }
            items(5) {
                LeaderboardItemShimmer()
            }
        }
    } else {
        LeaderboardList(modifier, uiState, handleIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardList(
    modifier: Modifier = Modifier,
    uiState: LeaderboardUiState,
    handleIntent: (LeaderboardIntentHandler) -> Unit,
) {
    val topThree = uiState.profiles
        .filter { it.position in 1..3 }
        .sortedBy { it.position } // Ensures consistent ordering: 1, 2, 3

    val rest = uiState.profiles
        .filter { it.position > 3 }
        .sortedBy { it.position }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        item {
            PodiumTopThree(profiles = topThree, handleIntent = handleIntent)
        }
        items(rest.size) { index ->
            LeaderboardItem(
                profile = rest[index],
                onClick = { handleIntent(LeaderboardIntentHandler.NavigateToProfile(it)) },
            )
        }
    }
}

@Preview
@Composable
fun LeaderboardScreenPreview(
    @PreviewParameter(LeaderboardTemplateProvider::class) data: LeaderboardData,
) {
    ThemedPreviewTemplate(data.isDark) {
        LeaderboardScreenContent(uiState = data.uiState, handleIntent = {})
    }
}
