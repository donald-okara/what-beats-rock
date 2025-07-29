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
package ke.don.feature_leaderboard.models

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ke.don.core_datasource.domain.models.PodiumProfile

data class LeaderboardData(
    val isDark: Boolean = false,
    val uiState: LeaderboardUiState,
)

class LeaderboardTemplateProvider : PreviewParameterProvider<LeaderboardData> {
    val fakeLeaderboardState = LeaderboardUiState(
        profiles = listOf(
            PodiumProfile(position = 1, score = 1500, profileUrl = null, userName = "Alice Wonder"),
            PodiumProfile(position = 2, score = 1400, profileUrl = null, userName = "Bob Roberts"),
            PodiumProfile(position = 3, score = 1300, profileUrl = null, userName = "Charlie Dunham"),
            PodiumProfile(position = 4, score = 1200, profileUrl = null, userName = "Diana Ross"),
            PodiumProfile(position = 5, score = 1100, profileUrl = null, userName = "Ethan Shunt"),
            PodiumProfile(position = 6, score = 1000, profileUrl = null, userName = "Fiona Shrekinson"),
            PodiumProfile(position = 7, score = 950, profileUrl = null, userName = "George Bush"),
            PodiumProfile(position = 8, score = 900, profileUrl = null, userName = "Hannah Montana"),
            PodiumProfile(position = 9, score = 850, profileUrl = null, userName = "Isaac Abrahamson"),
            PodiumProfile(position = 10, score = 800, profileUrl = null, userName = "Jade Victoria"),
            PodiumProfile(position = 11, score = 800, profileUrl = null, userName = "Jade Victoria"),
            PodiumProfile(position = 12, score = 800, profileUrl = null, userName = "Jade Victoria"),
            PodiumProfile(position = 13, score = 800, profileUrl = null, userName = "Jade Victoria"),
        ),
    )

    override val values: Sequence<LeaderboardData> = sequenceOf(
        LeaderboardData(
            isDark = false,
            uiState = fakeLeaderboardState,
        ),
        LeaderboardData(
            isDark = true,
            uiState = fakeLeaderboardState,
        ),
        LeaderboardData(
            isDark = false,
            uiState = fakeLeaderboardState.copy(isLoading = true),
        ),
        LeaderboardData(
            isDark = true,
            uiState = fakeLeaderboardState.copy(isLoading = true),
        ),

    )
}
