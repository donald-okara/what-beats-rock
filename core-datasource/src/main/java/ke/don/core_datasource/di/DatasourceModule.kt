package ke.don.core_datasource.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.don.core_datasource.ai.VertexProvider
import ke.don.core_datasource.ai.VertexProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {

    @Provides
    @Singleton
    fun provideVertexProvider(): VertexProvider {
        return VertexProviderImpl()
    }

}