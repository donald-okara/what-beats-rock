package ke.don.core_datasource.remote.repositories

import ke.don.core_datasource.domain.models.PodiumProfile
import ke.don.core_datasource.domain.repositories.LeaderboardRepository
import ke.don.core_datasource.remote.FirebaseApi

class LeaderboardRepositoryImpl(
    private val api: FirebaseApi
): LeaderboardRepository {
    override suspend fun fetchLeaderboard(): Result<List<PodiumProfile>> = api.fetchLeaderboard()
}