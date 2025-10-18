package ar.edu.unicen.seminario.core.data.api

import ar.edu.unicen.seminario.core.data.model.GameDto
import ar.edu.unicen.seminario.core.data.model.FilterDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RawgApiService {
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("platforms") platforms: String? = null,
        @Query("genres") genres: String? = null,
        @Query("publishers") publishers: String? = null,
        @Query("stores") stores: String? = null,
        @Query("ordering") ordering: String? = null,
        @Query("search") search: String? = null
    ): Response<GameDto.GameListResponse>

    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") gameId: Int,
        @Query("key") apiKey: String
    ): Response<GameDto.GameDetailResponse>

    @GET("platforms")
    suspend fun getPlatforms(
        @Query("key") apiKey: String,
        @Query("page_size") pageSize: Int = 100
    ): Response<FilterDto.PlatformListResponse>

    @GET("genres")
    suspend fun getGenres(
        @Query("key") apiKey: String,
        @Query("page_size") pageSize: Int = 100
    ): Response<FilterDto.GenreListResponse>

    @GET("publishers")
    suspend fun getPublishers(
        @Query("key") apiKey: String,
        @Query("page_size") pageSize: Int = 100
    ): Response<FilterDto.PublisherListResponse>

    @GET("stores")
    suspend fun getStores(
        @Query("key") apiKey: String,
        @Query("page_size") pageSize: Int = 100
    ): Response<FilterDto.StoreListResponse>
}
