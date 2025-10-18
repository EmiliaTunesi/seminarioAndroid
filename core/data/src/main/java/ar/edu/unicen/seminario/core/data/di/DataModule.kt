package ar.edu.unicen.seminario.core.data.di

import ar.edu.unicen.seminario.core.data.BuildConfig
import ar.edu.unicen.seminario.core.data.api.RawgApiService
import ar.edu.unicen.seminario.core.data.mapper.GameMapper
import ar.edu.unicen.seminario.core.data.repository.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideRawgApiService(): RawgApiService =
        Retrofit.Builder()
            .baseUrl("https://api.rawg.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RawgApiService::class.java)

    @Provides
    @Singleton
    fun provideGameMapper(): GameMapper = GameMapper()

    @Provides
    @Named("api_key")
    fun provideApiKey(): String = BuildConfig.API_KEY

    @Provides
    @Singleton
    fun provideGameRepository(
        apiService: RawgApiService,
        mapper: GameMapper,
        @Named("api_key") apiKey: String
    ): GameRepository = GameRepository(apiService, mapper, apiKey)
}
