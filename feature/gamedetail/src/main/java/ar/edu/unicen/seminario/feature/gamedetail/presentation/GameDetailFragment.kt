package ar.edu.unicen.seminario.feature.gamedetail.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import ar.edu.unicen.seminario.core.navigation.NavigationArgs
import ar.edu.unicen.seminario.feature.gamedetail.databinding.FragmentGameDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment que muestra la información detallada de un videojuego específico
 */
@AndroidEntryPoint
class GameDetailFragment : Fragment() {

    private var _binding: FragmentGameDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameDetailViewModel by viewModels()

    private var gameId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el ID del juego de los argumentos
        gameId = arguments?.getInt(NavigationArgs.GAME_ID, -1) ?: -1

        if (gameId == -1) {
            // Error: no se proporcionó un ID válido
            showError("Error: ID de juego no válido")
            return
        }

        setupToolbar()
        setupErrorHandling()
        observeViewModel()

        // Cargar la información del juego
        viewModel.loadGameDetail(gameId)
    }

    /**
     * Configura la toolbar con navegación hacia atrás
     */
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * Configura el manejo de errores
     */
    private fun setupErrorHandling() {
        binding.errorLayout.btnRetry.setOnClickListener {
            viewModel.retry(gameId)
        }
    }

    /**
     * Observa los estados del ViewModel y actualiza la UI
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is GameDetailUiState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.contentLayout.isVisible = false
                        binding.errorLayout.root.isVisible = false
                    }
                    is GameDetailUiState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.contentLayout.isVisible = true
                        binding.errorLayout.root.isVisible = false

                        displayGameDetail(state.gameDetail)
                    }
                    is GameDetailUiState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.contentLayout.isVisible = false
                        binding.errorLayout.root.isVisible = true
                        binding.errorLayout.tvErrorMessage.text = state.message
                    }
                }
            }
        }
    }

    /**
     * Muestra la información detallada del juego en la UI
     */
    private fun displayGameDetail(gameDetail: ar.edu.unicen.seminario.core.data.model.domain.GameDetail) {
        binding.apply {
            // Configurar el título de la toolbar
            toolbar.title = gameDetail.name

            // Cargar la imagen principal
            ivGameImage.load(gameDetail.imageUrl) {
                crossfade(true)
                placeholder(ar.edu.unicen.seminario.core.ui.R.drawable.ic_error)
                error(ar.edu.unicen.seminario.core.ui.R.drawable.ic_error)
            }

            // Mostrar el nombre del juego
            tvGameName.text = gameDetail.name

            // Mostrar el rating si está disponible
            if (gameDetail.rating != null && gameDetail.rating > 0) {
                tvGameRating.text = "★ ${String.format("%.1f", gameDetail.rating)}"
                tvGameRating.isVisible = true
            } else {
                tvGameRating.isVisible = false
            }

            // Mostrar la fecha de lanzamiento
            if (!gameDetail.releaseDate.isNullOrEmpty()) {
                tvReleaseDate.text = formatReleaseDate(gameDetail.releaseDate)
                tvReleaseDateLabel.isVisible = true
                tvReleaseDate.isVisible = true
            } else {
                tvReleaseDateLabel.isVisible = false
                tvReleaseDate.isVisible = false
            }

            // Mostrar los géneros
            if (gameDetail.genres.isNotEmpty()) {
                tvGenres.text = gameDetail.genres.joinToString(", ")
                tvGenresLabel.isVisible = true
                tvGenres.isVisible = true
            } else {
                tvGenresLabel.isVisible = false
                tvGenres.isVisible = false
            }

            // Mostrar las plataformas
            if (gameDetail.platforms.isNotEmpty()) {
                tvPlatforms.text = gameDetail.platforms.joinToString(", ")
                tvPlatformsLabel.isVisible = true
                tvPlatforms.isVisible = true
            } else {
                tvPlatformsLabel.isVisible = false
                tvPlatforms.isVisible = false
            }

            // Mostrar los publishers
            if (gameDetail.publishers.isNotEmpty()) {
                tvPublishers.text = gameDetail.publishers.joinToString(", ")
                tvPublishersLabel.isVisible = true
                tvPublishers.isVisible = true
            } else {
                tvPublishersLabel.isVisible = false
                tvPublishers.isVisible = false
            }

            // Mostrar la descripción
            if (!gameDetail.description.isNullOrEmpty()) {
                tvDescription.text = gameDetail.description
                tvDescriptionLabel.isVisible = true
                tvDescription.isVisible = true
            } else {
                tvDescription.text = getString(ar.edu.unicen.seminario.core.ui.R.string.sin_resumen)
                tvDescriptionLabel.isVisible = true
                tvDescription.isVisible = true
            }
        }
    }

    /**
     * Formatea la fecha de lanzamiento para mostrarla de forma legible
     */
    private fun formatReleaseDate(releaseDate: String): String {
        return try {
            // La fecha viene en formato YYYY-MM-DD, la convertimos a formato más legible
            val parts = releaseDate.split("-")
            if (parts.size == 3) {
                val year = parts[0]
                val month = when (parts[1]) {
                    "01" -> "enero"
                    "02" -> "febrero"
                    "03" -> "marzo"
                    "04" -> "abril"
                    "05" -> "mayo"
                    "06" -> "junio"
                    "07" -> "julio"
                    "08" -> "agosto"
                    "09" -> "septiembre"
                    "10" -> "octubre"
                    "11" -> "noviembre"
                    "12" -> "diciembre"
                    else -> parts[1]
                }
                val day = parts[2].toIntOrNull()?.toString() ?: parts[2]
                "$day de $month, $year"
            } else {
                releaseDate
            }
        } catch (e: Exception) {
            releaseDate
        }
    }

    /**
     * Muestra un error genérico
     */
    private fun showError(message: String) {
        binding.progressBar.isVisible = false
        binding.contentLayout.isVisible = false
        binding.errorLayout.root.isVisible = true
        binding.errorLayout.tvErrorMessage.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
