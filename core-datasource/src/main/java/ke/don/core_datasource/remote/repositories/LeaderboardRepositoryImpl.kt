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
package ke.don.core_datasource.remote.repositories

import ke.don.core_datasource.domain.models.PodiumProfile
import ke.don.core_datasource.domain.repositories.LeaderboardRepository
import ke.don.core_datasource.remote.FirebaseApi

class LeaderboardRepositoryImpl(
    private val api: FirebaseApi,
) : LeaderboardRepository {
    override suspend fun fetchLeaderboard(): Result<List<PodiumProfile>> = api.fetchLeaderboard()
}
