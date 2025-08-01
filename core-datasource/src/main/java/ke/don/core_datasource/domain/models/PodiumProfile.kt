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

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PodiumProfile(
    val position: Int = 0, // 1, 2, 3 (not necessarily in order)
    val score: Int = 0,
    val profileUrl: String? = null,
    val lastPlayed: Long? = null,
    val createdAt: String? = null,
    val userName: String = "",
    val id: String = "",
    val isCurrentUser: Boolean = false,
) : Parcelable
