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
package ke.don.core_datasource.domain.models

data class Profile(
    var uid: String? = null,
    var displayName: String? = null,
    var email: String? = null,
    var photoUrl: String? = null,
    var createdAt: String? = null,
    var highScore: Int? = 0,
    var onboarded: Boolean? = null,
    val lastPlayed: Long? = null,
){
    fun toPodiumProfile(): PodiumProfile {
        return PodiumProfile(
            score = highScore ?: 0,
            profileUrl = photoUrl,
            userName = displayName ?: "",
            id = uid ?: ""
        )
    }
}
