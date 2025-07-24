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

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ke.don.core_datasource.domain.models.Profile
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
            val uid = auth.currentUser?.uid ?: return@withContext Result.failure(Exception("User not authenticated"))
            firestore
                .collection("profiles")
                .document(uid)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
