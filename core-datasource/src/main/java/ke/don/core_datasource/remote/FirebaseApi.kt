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
package ke.don.core_datasource.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ke.don.core_datasource.domain.models.PodiumProfile
import ke.don.core_datasource.domain.models.Profile
import ke.don.core_datasource.domain.models.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseApi {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

            val profile = snapshot.toObject(Profile::class.java) ?: Profile()

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMyProfile(): Result<Profile> = withContext(Dispatchers.IO) {
        try {
            val id = auth.currentUser?.uid

            if (id == null) {
                Result.failure(Exception("User not authenticated"))
            } 
            else {
                val snapshot = firestore
                    .collection("profiles")
                    .document(id)
                    .get()
                    .await()

                if (!snapshot.exists()) {
                    return@withContext Result.failure(Exception("Profile not found"))
                }

                val profile = snapshot.toObject(Profile::class.java) ?: Profile()

                Result.success(profile)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPodiumProfileById(uid: String): Result<PodiumProfile> = withContext(Dispatchers.IO) {
        try {
            val userSnapshot = firestore
                .collection("profiles")
                .document(uid)
                .get()
                .await()

            if (!userSnapshot.exists()) {
                return@withContext Result.failure(Exception("Profile not found"))
            }

            val profile = userSnapshot.toObject(Profile::class.java)?.copy(uid = uid)
                ?: return@withContext Result.failure(Exception("Invalid profile data"))

            val userScore = profile.highScore ?: 0

            // Count how many profiles have a higher score
            val higherScoreCount = firestore
                .collection("profiles")
                .whereGreaterThan("highScore", userScore)
                .get()
                .await()
                .size()

            val rank = higherScoreCount + 1

            val podiumProfile = profile.toPodiumProfile().copy(
                position = rank,
                isCurrentUser = auth.currentUser?.uid == uid
            )

            Result.success(podiumProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyPodiumProfile(): Result<PodiumProfile> = withContext(Dispatchers.IO) {
        try {
            val uid= auth.currentUser?.uid
            if (uid == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            val userSnapshot = firestore
                .collection("profiles")
                .document(uid)
                .get()
                .await()

            if (!userSnapshot.exists()) {
                return@withContext Result.failure(Exception("Profile not found"))
            }

            val profile = userSnapshot.toObject(Profile::class.java)?.copy(uid = uid)
                ?: return@withContext Result.failure(Exception("Invalid profile data"))

            val userScore = profile.highScore ?: 0

            // Count how many profiles have a higher score
            val higherScoreCount = firestore
                .collection("profiles")
                .whereGreaterThan("highScore", userScore)
                .get()
                .await()
                .size()

            val rank = higherScoreCount + 1

            val podiumProfile = profile.toPodiumProfile().copy(
                position = rank,
                isCurrentUser = auth.currentUser?.uid == uid
            )

            Result.success(podiumProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchLeaderboard(): Result<List<PodiumProfile>> = withContext(Dispatchers.IO) {
        try {
            val currentUid = auth.currentUser?.uid
                ?: return@withContext Result.failure(Exception("User not authenticated"))

            // Fetch top 10
            val topSnapshots = firestore
                .collection("profiles")
                .orderBy("highScore", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            val topProfiles = topSnapshots.documents.mapNotNull { doc ->
                doc.toObject(Profile::class.java)?.copy(uid = doc.id)
            }.toMutableList()

            val isCurrentUserInTop = topProfiles.any { it.uid == currentUid }

            var currentUserProfile: Profile? = null
            var currentUserRank: Int? = null

            if (!isCurrentUserInTop) {
                // Fetch current user's profile
                val userSnapshot = firestore
                    .collection("profiles")
                    .document(currentUid)
                    .get()
                    .await()

                if (userSnapshot.exists()) {
                    currentUserProfile = userSnapshot.toObject(Profile::class.java)?.copy(uid = userSnapshot.id)
                    val userScore = currentUserProfile?.highScore ?: 0

                    // Count how many users have a higher score
                    val higherScoreDocs = firestore
                        .collection("profiles")
                        .whereGreaterThan("highScore", userScore)
                        .get()
                        .await()

                    currentUserRank = higherScoreDocs.size() + 1

                    currentUserProfile?.let {
                        topProfiles.add(currentUserProfile)
                    }
                }
            }

            val podium = topProfiles
                .sortedByDescending { it.highScore ?: 0 }
                .mapIndexed { index, profile ->
                    val isCurrent = profile.uid == currentUid
                    val position = if (isCurrent && currentUserRank != null) {
                        currentUserRank
                    } else {
                        index + 1
                    }

                    profile.toPodiumProfile().copy(
                        position = position,
                        isCurrentUser = isCurrent
                    )
                }

            Result.success(podium)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateHighScore(newScore: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid
            if (uid == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            val profileRef = firestore.collection("profiles").document(uid)

            // Only update if newScore is higher
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(profileRef)
                val currentHigh = snapshot.getLong("highScore") ?: 0L
                if (newScore > currentHigh) {
                    transaction.update(profileRef, "highScore", newScore)
                }
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun fetchOrInitializeUserSessions(): Result<List<Session>> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        val sessionsRef = firestore.collection("profiles").document(uid).collection("sessions")

        return try {
            val snapshot = sessionsRef.get().await()
            val sessions = snapshot.documents.mapNotNull { it.toObject(Session::class.java) }

            val allStarted = sessions.all { it.started == true }

            val updatedSessions = if (allStarted) {
                val newSession = Session()
                newSession.id?.let {
                    sessionsRef.document(it).set(newSession).await()
                    sessions + newSession
                } ?: return Result.failure(Exception("New session ID is null"))
            } else {
                sessions
            }

            Result.success(updatedSessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetUserSessions(): Result<List<Session>> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        val sessionsRef = firestore.collection("profiles").document(uid).collection("sessions")

        return try {
            // Delete existing sessions
            val snapshot = sessionsRef.get().await()
            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            // Create a new session with assigned ID
            val newSession = Session()
            newSession.id?.let { sessionsRef.document(it).set(newSession).await() } ?: return Result.failure(Exception("Session Id is null"))

            // Return the updated list
            Result.success(listOf(newSession))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchUserSessions(uid: String): Result<List<Session>> {
        val sessionsRef = firestore.collection("profiles").document(uid).collection("sessions")

        return try {
            val snapshot = sessionsRef.get().await()
            val sessions = snapshot.documents.mapNotNull { it.toObject(Session::class.java) }
            Result.success(sessions) // success even if list is empty
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMySessions(): Result<List<Session>> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        val sessionsRef = firestore.collection("profiles").document(uid).collection("sessions")

        return try {
            val snapshot = sessionsRef.get().await()
            val sessions = snapshot.documents.mapNotNull { it.toObject(Session::class.java) }
            Result.success(sessions) // success even if list is empty
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateUserSession(
        sessionId: String,
        updatedSession: Session
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        Log.d("FirebaseApi", "updateUserSession: $updatedSession")
        val sessionRef = firestore.collection("profiles")
            .document(uid)
            .collection("sessions")
            .document(sessionId)

        return try {
            sessionRef.set(updatedSession).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteOwnProfile(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid
                ?: return@withContext Result.failure(Exception("User not authenticated"))

            val userDocRef = firestore.collection("profiles").document(uid)
            val sessionsRef = userDocRef.collection("sessions")

            val sessionDocs = sessionsRef.get().await()
            val batch = firestore.batch()

            // Delete session documents
            for (doc in sessionDocs.documents) {
                batch.delete(doc.reference)
            }

            // Delete the user profile document
            batch.delete(userDocRef)

            // Commit batch
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
