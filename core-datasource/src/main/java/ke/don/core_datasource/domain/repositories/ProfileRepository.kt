package ke.don.core_datasource.domain.repositories

import ke.don.core_datasource.domain.models.Profile

interface ProfileRepository {
    suspend fun fetchProfile(id: String): Result<Profile>
    suspend fun fetchMyProfile(): Result<Profile>
    suspend fun signOut(): Result<Unit>
    suspend fun deleteOwnProfile(): Result<Unit>
}