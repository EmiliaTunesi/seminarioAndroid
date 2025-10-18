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

@HiltViewModel
class GameListViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _filters = MutableStateFlow(GameFilters())
    val filters: StateFlow<GameFilters> = _filters.asStateFlow()
    val gamesFlow = filters.flatMapLatest { filters ->
        gameRepository.getGamesStream(filters)
    }.cachedIn(viewModelScope)
    fun applyFilters(newFilters: GameFilters) {
        _filters.value = newFilters
    }

    fun clearFilters() {
        _filters.value = GameFilters()
    }

    fun hasActiveFilters(): Boolean {
        val filters = _filters.value
        return filters.platforms.isNotEmpty() ||
                filters.genres.isNotEmpty() ||
                filters.publishers.isNotEmpty() ||
                filters.stores.isNotEmpty() ||
                filters.ordering != "-added"
    }
}
