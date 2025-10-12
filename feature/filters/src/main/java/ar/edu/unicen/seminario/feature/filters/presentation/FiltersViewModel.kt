package ar.edu.unicen.seminario.feature.filters.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unicen.seminario.core.data.model.domain.*
import ar.edu.unicen.seminario.core.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estados de la UI para los filtros
 */
sealed class FiltersUiState {
    object Loading : FiltersUiState()
    data class Success(
        val platforms: List<Platform>,
        val genres: List<Genre>,
        val publishers: List<Publisher>,
        val stores: List<Store>
    ) : FiltersUiState()
    data class Error(val message: String) : FiltersUiState()
}

/**
 * ViewModel que maneja la lógica de la pantalla de filtros
 */
@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<FiltersUiState>(FiltersUiState.Loading)
    val uiState: StateFlow<FiltersUiState> = _uiState.asStateFlow()

    // Filtros seleccionados actualmente
    private val _selectedPlatforms = MutableStateFlow<Set<Int>>(emptySet())
    val selectedPlatforms: StateFlow<Set<Int>> = _selectedPlatforms.asStateFlow()

    private val _selectedGenres = MutableStateFlow<Set<Int>>(emptySet())
    val selectedGenres: StateFlow<Set<Int>> = _selectedGenres.asStateFlow()

    private val _selectedPublishers = MutableStateFlow<Set<Int>>(emptySet())
    val selectedPublishers: StateFlow<Set<Int>> = _selectedPublishers.asStateFlow()

    private val _selectedStores = MutableStateFlow<Set<Int>>(emptySet())
    val selectedStores: StateFlow<Set<Int>> = _selectedStores.asStateFlow()

    private val _selectedOrdering = MutableStateFlow(OrderingType.ADDED_DESC)
    val selectedOrdering: StateFlow<OrderingType> = _selectedOrdering.asStateFlow()

    init {
        loadFiltersData()
    }

    /**
     * Carga todos los datos de filtros desde la API
     */
    fun loadFiltersData() {
        viewModelScope.launch {
            _uiState.value = FiltersUiState.Loading

            try {
                // Cargar todas las listas de filtros en paralelo
                val platformsResult = gameRepository.getPlatforms()
                val genresResult = gameRepository.getGenres()
                val publishersResult = gameRepository.getPublishers()
                val storesResult = gameRepository.getStores()

                // Verificar que todas las llamadas fueron exitosas
                val platforms = platformsResult.getOrNull()
                val genres = genresResult.getOrNull()
                val publishers = publishersResult.getOrNull()
                val stores = storesResult.getOrNull()

                if (platforms != null && genres != null && publishers != null && stores != null) {
                    _uiState.value = FiltersUiState.Success(
                        platforms = platforms,
                        genres = genres,
                        publishers = publishers,
                        stores = stores
                    )
                } else {
                    // Determinar qué error mostrar
                    val error = platformsResult.exceptionOrNull()
                        ?: genresResult.exceptionOrNull()
                        ?: publishersResult.exceptionOrNull()
                        ?: storesResult.exceptionOrNull()
                        ?: Exception("Error desconocido al cargar filtros")

                    _uiState.value = FiltersUiState.Error(
                        message = getErrorMessage(error)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FiltersUiState.Error(
                    message = getErrorMessage(e)
                )
            }
        }
    }

    /**
     * Establece los filtros iniciales (útil cuando se viene de una búsqueda con filtros previos)
     */
    fun setInitialFilters(filters: GameFilters) {
        _selectedPlatforms.value = filters.platforms.toSet()
        _selectedGenres.value = filters.genres.toSet()
        _selectedPublishers.value = filters.publishers.toSet()
        _selectedStores.value = filters.stores.toSet()

        // Encontrar el tipo de ordenamiento correspondiente
        val orderingType = OrderingType.values().find { it.value == filters.ordering }
            ?: OrderingType.ADDED_DESC
        _selectedOrdering.value = orderingType
    }

    /**
     * Alterna la selección de una plataforma
     */
    fun togglePlatform(platformId: Int) {
        val current = _selectedPlatforms.value.toMutableSet()
        if (current.contains(platformId)) {
            current.remove(platformId)
        } else {
            current.add(platformId)
        }
        _selectedPlatforms.value = current
    }

    /**
     * Alterna la selección de un género
     */
    fun toggleGenre(genreId: Int) {
        val current = _selectedGenres.value.toMutableSet()
        if (current.contains(genreId)) {
            current.remove(genreId)
        } else {
            current.add(genreId)
        }
        _selectedGenres.value = current
    }

    /**
     * Alterna la selección de un publisher
     */
    fun togglePublisher(publisherId: Int) {
        val current = _selectedPublishers.value.toMutableSet()
        if (current.contains(publisherId)) {
            current.remove(publisherId)
        } else {
            current.add(publisherId)
        }
        _selectedPublishers.value = current
    }

    /**
     * Alterna la selección de una tienda
     */
    fun toggleStore(storeId: Int) {
        val current = _selectedStores.value.toMutableSet()
        if (current.contains(storeId)) {
            current.remove(storeId)
        } else {
            current.add(storeId)
        }
        _selectedStores.value = current
    }

    /**
     * Establece el tipo de ordenamiento
     */
    fun setOrdering(orderingType: OrderingType) {
        _selectedOrdering.value = orderingType
    }

    /**
     * Limpia todos los filtros seleccionados
     */
    fun clearAllFilters() {
        _selectedPlatforms.value = emptySet()
        _selectedGenres.value = emptySet()
        _selectedPublishers.value = emptySet()
        _selectedStores.value = emptySet()
        _selectedOrdering.value = OrderingType.ADDED_DESC
    }

    /**
     * Genera el objeto GameFilters con las selecciones actuales
     */
    fun getCurrentFilters(): GameFilters {
        return GameFilters(
            platforms = _selectedPlatforms.value.toList(),
            genres = _selectedGenres.value.toList(),
            publishers = _selectedPublishers.value.toList(),
            stores = _selectedStores.value.toList(),
            ordering = _selectedOrdering.value.value
        )
    }

    /**
     * Verifica si hay filtros aplicados
     */
    fun hasActiveFilters(): Boolean {
        return _selectedPlatforms.value.isNotEmpty() ||
                _selectedGenres.value.isNotEmpty() ||
                _selectedPublishers.value.isNotEmpty() ||
                _selectedStores.value.isNotEmpty() ||
                _selectedOrdering.value != OrderingType.ADDED_DESC
    }

    /**
     * Convierte excepciones en mensajes de error legibles
     */
    private fun getErrorMessage(throwable: Throwable): String {
        return when {
            throwable.message?.contains("network", ignoreCase = true) == true ||
            throwable.message?.contains("connection", ignoreCase = true) == true -> {
                "Error de conexión. Verificá tu conexión a internet."
            }
            else -> "Ocurrió un error al cargar los filtros. Intentá nuevamente."
        }
    }
}
