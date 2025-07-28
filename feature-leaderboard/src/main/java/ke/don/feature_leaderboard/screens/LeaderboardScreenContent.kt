package ke.don.feature_leaderboard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
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
import ke.don.feature_leaderboard.models.LeaderboardTemplateProvider
import ke.don.feature_leaderboard.models.LeaderboardUiState

@Composable
fun LeaderboardScreenContent(
    modifier: Modifier = Modifier,
    uiState: LeaderboardUiState
){
    if (uiState.isLoading){
        LazyColumn {
            item {
                PodiumTopThreeShimmer()
            }
            items(5) {
                LeaderboardItemShimmer()
            }
        }
    } else {
        LeaderboardList(modifier, uiState)
    }
}

@Composable
fun LeaderboardList(
    modifier: Modifier = Modifier,
    uiState: LeaderboardUiState
){
    val topThree = uiState.profiles
        .filter { it.position in 1..3 }
        .sortedBy { it.position } // Ensures consistent ordering: 1, 2, 3

    val rest = uiState.profiles
        .filter { it.position > 3 }
        .sortedBy { it.position }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        stickyHeader {
            PodiumTopThree(profiles = topThree)
        }
        items(rest.size){ index ->
            LeaderboardItem(
                profile = rest[index]
            )
        }
    }
}

@Preview
@Composable
fun LeaderboardScreenPreview(
    @PreviewParameter(LeaderboardTemplateProvider ::class) data: LeaderboardData
){
    ThemedPreviewTemplate(data.isDark) {
        LeaderboardScreenContent(uiState = data.uiState)
    }
}