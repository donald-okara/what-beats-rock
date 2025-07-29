package ke.don.core_datasource.domain.repositories

import ke.don.core_datasource.domain.models.PodiumProfile

interface LeaderboardRepository {
    suspend fun fetchLeaderboard(): Result<List<PodiumProfile>>
}