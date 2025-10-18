package ar.edu.unicen.seminario.feature.filters.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unicen.seminario.core.data.model.domain.*
import ar.edu.unicen.seminario.core.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FilterSectionState<T>(
    val loading: Boolean = false,
    val error: String? = null,
    val data: List<T> = emptyList()
)

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    val platformsState = MutableStateFlow(FilterSectionState<Platform>(loading = true))
    val genresState = MutableStateFlow(FilterSectionState<Genre>(loading = true))
    val publishersState = MutableStateFlow(FilterSectionState<Publisher>(loading = true))
    val storesState = MutableStateFlow(FilterSectionState<Store>(loading = true))

    private val _selectedPlatforms = MutableStateFlow<Set<Int>>(emptySet())
    val selectedPlatforms: StateFlow<Set<Int>> = _selectedPlatforms.asStateFlow()

    private val _selectedGenres = MutableStateFlow<Set<Int>>(emptySet())
    val selectedGenres: StateFlow<Set<Int>> = _selectedGenres.asStateFlow()

    private val _selectedPublishers = MutableStateFlow<Set<Int>>(emptySet())
    val selectedPublishers: StateFlow<Set<Int>> = _selectedPublishers.asStateFlow()

    private val _selectedStores = MutableStateFlow<Set<Int>>(emptySet())
    val selectedStores: StateFlow<Set<Int>> = _selectedStores.asStateFlow()

    private val _selectedOrdering = MutableStateFlow(OrderingType.ADDED)
    val selectedOrdering: StateFlow<OrderingType> = _selectedOrdering.asStateFlow()
    val hasActiveFiltersFlow: StateFlow<Boolean> = combine(
        _selectedPlatforms,
        _selectedGenres,
        _selectedPublishers,
        _selectedStores,
        _selectedOrdering
    ) { platforms, genres, publishers, stores, ordering ->
        platforms.isNotEmpty() || genres.isNotEmpty() || publishers.isNotEmpty() || stores.isNotEmpty() || ordering != OrderingType.ADDED
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun loadPlatforms() {
        viewModelScope.launch {
            platformsState.value = FilterSectionState(loading = true)
            val result = gameRepository.getPlatforms()
            platformsState.value = result.fold(
                onSuccess = { FilterSectionState(data = it) },
                onFailure = { FilterSectionState(error = getErrorMessage(it)) }
            )
        }
    }

    fun loadGenres() {
        viewModelScope.launch {
            genresState.value = FilterSectionState(loading = true)
            val result = gameRepository.getGenres()
            genresState.value = result.fold(
                onSuccess = { FilterSectionState(data = it) },
                onFailure = { FilterSectionState(error = getErrorMessage(it)) }
            )
        }
    }

    fun loadPublishers() {
        viewModelScope.launch {
            publishersState.value = FilterSectionState(loading = true)
            val result = gameRepository.getPublishers()
            publishersState.value = result.fold(
                onSuccess = { FilterSectionState(data = it) },
                onFailure = { FilterSectionState(error = getErrorMessage(it)) }
            )
        }
    }

    fun loadStores() {
        viewModelScope.launch {
            storesState.value = FilterSectionState(loading = true)
            val result = gameRepository.getStores()
            storesState.value = result.fold(
                onSuccess = { FilterSectionState(data = it) },
                onFailure = { FilterSectionState(error = getErrorMessage(it)) }
            )
        }
    }

    fun loadAllFilters() {
        loadPlatforms()
        loadGenres()
        loadPublishers()
        loadStores()
    }

    init {
        loadAllFilters()
    }

    fun setInitialFilters(filters: GameFilters) {
        _selectedPlatforms.value = filters.platforms.toSet()
        _selectedGenres.value = filters.genres.toSet()
        _selectedPublishers.value = filters.publishers.toSet()
        _selectedStores.value = filters.stores.toSet()

        val orderingType = OrderingType.values().find { it.value == filters.ordering }
            ?: OrderingType.ADDED
        _selectedOrdering.value = orderingType
    }

    fun togglePlatform(platformId: Int) {
        val current = _selectedPlatforms.value.toMutableSet()
        if (current.contains(platformId)) {
            current.remove(platformId)
        } else {
            current.add(platformId)
        }
        _selectedPlatforms.value = current
    }

    fun toggleGenre(genreId: Int) {
        val current = _selectedGenres.value.toMutableSet()
        if (current.contains(genreId)) {
            current.remove(genreId)
        } else {
            current.add(genreId)
        }
        _selectedGenres.value = current
    }

    fun togglePublisher(publisherId: Int) {
        val current = _selectedPublishers.value.toMutableSet()
        if (current.contains(publisherId)) {
            current.remove(publisherId)
        } else {
            current.add(publisherId)
        }
        _selectedPublishers.value = current
    }

    fun toggleStore(storeId: Int) {
        val current = _selectedStores.value.toMutableSet()
        if (current.contains(storeId)) {
            current.remove(storeId)
        } else {
            current.add(storeId)
        }
        _selectedStores.value = current
    }

    fun setOrdering(orderingType: OrderingType) {
        _selectedOrdering.value = orderingType
    }

    fun clearAllFilters() {
        _selectedPlatforms.value = emptySet()
        _selectedGenres.value = emptySet()
        _selectedPublishers.value = emptySet()
        _selectedStores.value = emptySet()
        _selectedOrdering.value = OrderingType.ADDED
    }

    fun getCurrentFilters(): GameFilters {
        return GameFilters(
            platforms = _selectedPlatforms.value.toList(),
            genres = _selectedGenres.value.toList(),
            publishers = _selectedPublishers.value.toList(),
            stores = _selectedStores.value.toList(),
            ordering = _selectedOrdering.value.value
        )
    }

    fun hasActiveFilters(): Boolean {
        return _selectedPlatforms.value.isNotEmpty() ||
                _selectedGenres.value.isNotEmpty() ||
                _selectedPublishers.value.isNotEmpty() ||
                _selectedStores.value.isNotEmpty() ||
                _selectedOrdering.value != OrderingType.ADDED
    }

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
