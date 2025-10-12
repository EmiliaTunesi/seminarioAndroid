package ar.edu.unicen.seminario.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import ar.edu.unicen.seminario.core.data.api.RawgApiService
import ar.edu.unicen.seminario.core.data.mapper.GameMapper
import ar.edu.unicen.seminario.core.data.model.domain.*
import ar.edu.unicen.seminario.core.data.paging.GamesPagingSource
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Repositorio que maneja toda la comunicación con la API y el mapeo de datos
 * Implementa Paging 3 según las mejores prácticas de Android Jetpack
 */
@Singleton
class GameRepository @Inject constructor(
    private val apiService: RawgApiService,
    private val mapper: GameMapper,
    @Named("api_key") private val apiKey: String
) {

    /**
     * Obtiene un flujo de datos paginados de videojuegos aplicando filtros
     * Implementa Paging 3 con configuración optimizada
     */
    fun getGamesStream(filters: GameFilters): Flow<PagingData<Game>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false, // Deshabilitado para mejor UX
                prefetchDistance = 3, // Precargar 3 elementos antes del final
                initialLoadSize = 20, // Primera carga del mismo tamaño que las siguientes
                maxSize = PagingConfig.MAX_SIZE_UNBOUNDED // Sin límite de elementos en memoria
            ),
            pagingSourceFactory = {
                GamesPagingSource(
                    apiService = apiService,
                    apiKey = apiKey,
                    filters = filters,
                    mapper = mapper
                )
            }
        ).flow
    }

    /**
     * Obtiene información detallada de un videojuego específico
     */
    suspend fun getGameDetail(gameId: Int): Result<GameDetail> {
        return try {
            val response = apiService.getGameDetail(apiKey, gameId)

            if (response.isSuccessful) {
                val gameDetailDto = response.body()
                if (gameDetailDto != null) {
                    val gameDetail = mapper.mapToGameDetail(gameDetailDto)
                    Result.success(gameDetail)
                } else {
                    Result.failure(Exception("No se pudo obtener la información del juego"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene lista de géneros disponibles para filtros
     */
    suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val response = apiService.getGenres(apiKey)

            if (response.isSuccessful) {
                val genresDto = response.body()?.results
                if (genresDto != null) {
                    val genres = mapper.mapToGenreList(genresDto)
                    Result.success(genres)
                } else {
                    Result.failure(Exception("No se pudieron obtener los géneros"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene lista de plataformas disponibles para filtros
     */
    suspend fun getPlatforms(): Result<List<Platform>> {
        return try {
            val response = apiService.getPlatforms(apiKey)

            if (response.isSuccessful) {
                val platformsDto = response.body()?.results
                if (platformsDto != null) {
                    val platforms = mapper.mapToPlatformList(platformsDto)
                    Result.success(platforms)
                } else {
                    Result.failure(Exception("No se pudieron obtener las plataformas"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene lista de publishers disponibles para filtros
     */
    suspend fun getPublishers(): Result<List<Publisher>> {
        return try {
            val response = apiService.getPublishers(apiKey)

            if (response.isSuccessful) {
                val publishersDto = response.body()?.results
                if (publishersDto != null) {
                    val publishers = mapper.mapToPublisherList(publishersDto)
                    Result.success(publishers)
                } else {
                    Result.failure(Exception("No se pudieron obtener los publishers"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene lista de tiendas disponibles para filtros
     */
    suspend fun getStores(): Result<List<Store>> {
        return try {
            val response = apiService.getStores(apiKey)

            if (response.isSuccessful) {
                val storesDto = response.body()?.results
                if (storesDto != null) {
                    val stores = mapper.mapToStoreList(storesDto)
                    Result.success(stores)
                } else {
                    Result.failure(Exception("No se pudieron obtener las tiendas"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
