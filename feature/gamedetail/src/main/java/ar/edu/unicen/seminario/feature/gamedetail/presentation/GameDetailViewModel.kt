package ar.edu.unicen.seminario.feature.gamedetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unicen.seminario.core.data.model.domain.GameDetail
import ar.edu.unicen.seminario.core.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GameDetailUiState {
    object Loading : GameDetailUiState()
    data class Success(val gameDetail: GameDetail) : GameDetailUiState()
    data class Error(val message: String) : GameDetailUiState()
}

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<GameDetailUiState>(GameDetailUiState.Loading)
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    fun loadGameDetail(gameId: Int) {
        viewModelScope.launch {
            _uiState.value = GameDetailUiState.Loading
            try {
                val result = gameRepository.getGameDetail(gameId)
                result.onSuccess { gameDetail ->
                    _uiState.value = GameDetailUiState.Success(gameDetail)
                }.onFailure { throwable ->
                    _uiState.value = GameDetailUiState.Error(message = getErrorMessage(throwable))
                }
            } catch (e: Exception) {
                _uiState.value = GameDetailUiState.Error(message = e.message ?: "Error desconocido")
            }
        }
    }

    private fun getErrorMessage(throwable: Throwable): String {
        return throwable.message ?: "Error desconocido"
    }
}
