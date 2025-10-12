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

/**
 * Estados de la UI para la pantalla de detalle del juego
 */
sealed class GameDetailUiState {
    object Loading : GameDetailUiState()
    data class Success(val gameDetail: GameDetail) : GameDetailUiState()
    data class Error(val message: String) : GameDetailUiState()
}

/**
 * ViewModel que maneja la lógica de la pantalla de detalle de un videojuego
 */
@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<GameDetailUiState>(GameDetailUiState.Loading)
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    /**
     * Carga la información detallada de un videojuego específico
     */
    fun loadGameDetail(gameId: Int) {
        viewModelScope.launch {
            _uiState.value = GameDetailUiState.Loading

            try {
                val result = gameRepository.getGameDetail(gameId)

                result.onSuccess { gameDetail ->
                    _uiState.value = GameDetailUiState.Success(gameDetail)
                }.onFailure { throwable ->
                    _uiState.value = GameDetailUiState.Error(
                        message = getErrorMessage(throwable)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = GameDetailUiState.Error(
                    message = getErrorMessage(e)
                )
            }
        }
    }

    /**
     * Reintenta cargar la información del juego
     */
    fun retry(gameId: Int) {
        loadGameDetail(gameId)
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
            throwable.message?.contains("404") == true -> {
                "El juego solicitado no fue encontrado."
            }
            else -> "Ocurrió un error al cargar la información del juego. Intentá nuevamente."
        }
    }
}
