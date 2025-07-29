package ke.don.feature_leaderboard.models

import ke.don.core_datasource.domain.models.PodiumProfile

data class LeaderboardUiState(
    val profiles: List<PodiumProfile> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null
)
