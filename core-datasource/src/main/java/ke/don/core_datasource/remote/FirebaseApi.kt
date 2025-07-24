package ke.don.core_datasource.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ke.don.core_datasource.domain.models.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseApi {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    suspend fun fetchProfile(id: String): Result<Profile> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore
                .collection("profiles")
                .document(id)
                .get()
                .await()

            if (!snapshot.exists()) {
                return@withContext Result.failure(Exception("Profile not found"))
            }

            val profile = Profile(
                displayName = snapshot.getString("name") ?: "",
                email = snapshot.getString("email") ?: "",
                photoUrl = snapshot.getString("photoURL") ?: "",
                highScore = snapshot.getLong("highScore")?.toInt() ?: 0,
                createdAt = snapshot.getString("createdAt") ?: "",
                lastPlayed = snapshot.getLong("lastPlayed") // may be null
            )

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMyProfile(): Result<Profile> = withContext(Dispatchers.IO) {
        try {
            val id = FirebaseAuth.getInstance().currentUser?.uid

            if(id == null) {
                Result.failure(Exception("User not authenticated"))
            }else{
                val snapshot = firestore
                    .collection("profiles")
                    .document(id)
                    .get()
                    .await()

                if (!snapshot.exists()) {
                    return@withContext Result.failure(Exception("Profile not found"))
                }

                val profile = Profile(
                    displayName = snapshot.getString("name") ?: "",
                    email = snapshot.getString("email") ?: "",
                    photoUrl = snapshot.getString("photoURL") ?: "",
                    highScore = snapshot.getLong("highScore")?.toInt() ?: 0,
                    createdAt = snapshot.getString("createdAt") ?: "",
                    lastPlayed = snapshot.getLong("lastPlayed") // may be null
                )

                Result.success(profile)
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}