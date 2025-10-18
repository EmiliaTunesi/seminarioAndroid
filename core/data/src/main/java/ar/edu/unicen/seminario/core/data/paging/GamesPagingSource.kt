package ar.edu.unicen.seminario.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ar.edu.unicen.seminario.core.data.api.RawgApiService
import ar.edu.unicen.seminario.core.data.mapper.GameMapper
import ar.edu.unicen.seminario.core.data.model.domain.Game
import ar.edu.unicen.seminario.core.data.model.domain.GameFilters
import retrofit2.HttpException
import java.io.IOException

/**
 * PagingSource que maneja la paginación de videojuegos desde la API RAWG
 * Implementa la interfaz de Paging 3 de Android Jetpack
 */
class GamesPagingSource(
    private val apiService: RawgApiService,
    private val apiKey: String,
    private val filters: GameFilters,
    private val mapper: GameMapper
) : PagingSource<Int, Game>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
        private const val PAGE_SIZE = 20
    }

    /**
     * Carga una página de datos de la API RAWG
     * Este es el método principal que Paging 3 llama para cargar datos
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        return try {
            val page = params.key ?: STARTING_PAGE_INDEX

            val platformsParam = if (filters.platforms.isNotEmpty()) {
                filters.platforms.joinToString(",")
            } else null
            val genresParam = if (filters.genres.isNotEmpty()) {
                filters.genres.joinToString(",")
            } else null
            val publishersParam = if (filters.publishers.isNotEmpty()) {
                filters.publishers.joinToString(",")
            } else null
            val storesParam = if (filters.stores.isNotEmpty()) {
                filters.stores.joinToString(",")
            } else null

            // LOG: parámetros enviados a la API
            android.util.Log.d("GamesPagingSource", "Llamando API RAWG: page=$page, platforms=$platformsParam, genres=$genresParam, publishers=$publishersParam, stores=$storesParam, ordering=${filters.ordering}")

            val response = apiService.getGames(
                apiKey = apiKey,
                page = page,
                pageSize = PAGE_SIZE,
                platforms = platformsParam,
                genres = genresParam,
                publishers = publishersParam,
                stores = storesParam,
                ordering = filters.ordering
            )

            android.util.Log.d("GamesPagingSource", "Respuesta API: isSuccessful=${response.isSuccessful}, code=${response.code()}, body=${response.body()}")

            if (response.isSuccessful) {
                val data = response.body()
                val games = data?.results?.let { mapper.mapToGameList(it) } ?: emptyList()
                android.util.Log.d("GamesPagingSource", "Cantidad de juegos mapeados: ${games.size}")

                LoadResult.Page(
                    data = games,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = if (data?.next == null) null else page + 1
                )
            } else {
                android.util.Log.e("GamesPagingSource", "Error en respuesta API: ${response.errorBody()?.string()}")
                LoadResult.Error(HttpException(response))
            }
        } catch (exception: IOException) {
            android.util.Log.e("GamesPagingSource", "IOException: ${exception.message}")
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            android.util.Log.e("GamesPagingSource", "HttpException: ${exception.message}")
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            android.util.Log.e("GamesPagingSource", "Exception: ${exception.message}")
            LoadResult.Error(exception)
        }
    }

    /**
     * Proporciona una clave de actualización cuando se necesita refrescar los datos
     * Paging 3 usa esto para determinar qué página cargar después de invalidar
     */
    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
