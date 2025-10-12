package ar.edu.unicen.seminario.feature.gamelist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import ar.edu.unicen.seminario.core.data.model.domain.Game
import ar.edu.unicen.seminario.core.data.model.domain.GameFilters
import ar.edu.unicen.seminario.core.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * ViewModel que maneja el estado y la lógica de la pantalla de lista de juegos
 * Implementa Paging 3 con cachedIn() para optimizar el rendimiento
 */
@HiltViewModel
class GameListViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    // Estado actual de los filtros aplicados
    private val _currentFilters = MutableStateFlow(GameFilters())
    val currentFilters: StateFlow<GameFilters> = _currentFilters.asStateFlow()

    // Flujo de datos paginados que se actualiza automáticamente cuando cambian los filtros
    // cachedIn() es crucial para Paging 3 según la documentación oficial
    val gamesFlow: Flow<PagingData<Game>> = _currentFilters
        .flatMapLatest { filters ->
            gameRepository.getGamesStream(filters)
        }
        .cachedIn(viewModelScope)

    /**
     * Aplica nuevos filtros a la búsqueda de juegos
     * Esto provocará que se recargue la lista desde la página 1
     */
    fun applyFilters(filters: GameFilters) {
        _currentFilters.value = filters
    }

    /**
     * Limpia todos los filtros y vuelve al estado inicial
     */
    fun clearFilters() {
        _currentFilters.value = GameFilters()
    }

    /**
     * Verifica si hay filtros aplicados actualmente
     */
    fun hasActiveFilters(): Boolean {
        val filters = _currentFilters.value
        return filters.platforms.isNotEmpty() ||
                filters.genres.isNotEmpty() ||
                filters.publishers.isNotEmpty() ||
                filters.stores.isNotEmpty() ||
                filters.ordering != "-added"
    }
}
