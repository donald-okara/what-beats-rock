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
package ke.don.core_datasource.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.don.core_datasource.domain.repositories.LeaderboardRepository
import ke.don.core_datasource.domain.repositories.ProfileRepository
import ke.don.core_datasource.domain.use_cases.ChatUseCase
import ke.don.core_datasource.domain.use_cases.ChatUseCaseImpl
import ke.don.core_datasource.remote.FirebaseApi
import ke.don.core_datasource.remote.ai.VertexProvider
import ke.don.core_datasource.remote.ai.VertexProviderImpl
import ke.don.core_datasource.remote.repositories.LeaderboardRepositoryImpl
import ke.don.core_datasource.remote.repositories.ProfileRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {

    @Provides
    @Singleton
    fun provideVertexProvider(): VertexProvider {
        return VertexProviderImpl()
    }

    @Provides
    @Singleton
    fun provideFirebaseApi(): FirebaseApi = FirebaseApi()

    @Provides
    @Singleton
    fun provideProfileRepository(
        api: FirebaseApi,
    ): ProfileRepository = ProfileRepositoryImpl(api = api)

    @Provides
    @Singleton
    fun provideChatUseCase(
        api: FirebaseApi,
        ai: VertexProvider
    ): ChatUseCase = ChatUseCaseImpl(api = api, ai = ai)

    @Provides
    @Singleton
    fun provideLeaderboardRepository(
        api: FirebaseApi
    ): LeaderboardRepository = LeaderboardRepositoryImpl(api = api)
}
