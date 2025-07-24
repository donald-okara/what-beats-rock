package ke.don.core_datasource.remote.repositories

import ke.don.core_datasource.domain.models.Profile
import ke.don.core_datasource.domain.repositories.ProfileRepository
import ke.don.core_datasource.remote.FirebaseApi

class ProfileRepositoryImpl(
    private val api: FirebaseApi
): ProfileRepository {
    override suspend fun fetchProfile(id: String): Result<Profile> = api.fetchProfile(id)
    override suspend fun fetchMyProfile(): Result<Profile> = api.fetchMyProfile()
    override suspend fun signOut(): Result<Unit> = api.signOut()
    override suspend fun deleteOwnProfile(): Result<Unit> = api.deleteOwnProfile()
}