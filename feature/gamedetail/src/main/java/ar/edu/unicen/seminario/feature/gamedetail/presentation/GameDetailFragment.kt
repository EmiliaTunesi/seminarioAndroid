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
import ar.edu.unicen.seminario.feature.gamedetail.databinding.FragmentGameDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import kotlinx.coroutines.launch
import ar.edu.unicen.seminario.core.ui.R
import ar.edu.unicen.seminario.core.data.model.domain.GameDetail

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
        gameId = arguments?.getInt("gameId", -1) ?: -1
        if (gameId == -1) {
            findNavController().navigateUp()
            return
        }
        binding.errorLayout.isVisible = false
        binding.contentLayout.isVisible = false
        binding.progressBar.isVisible = false
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.btnRetry.setOnClickListener {
            viewModel.loadGameDetail(gameId)
        }
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is GameDetailUiState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.errorLayout.isVisible = false
                        binding.contentLayout.isVisible = false
                    }
                    is GameDetailUiState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.errorLayout.isVisible = false
                        binding.contentLayout.isVisible = true
                        bindGameDetail(state.gameDetail)
                    }
                    is GameDetailUiState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.errorLayout.isVisible = true
                        binding.contentLayout.isVisible = false
                        binding.tvErrorMessage.text = state.message ?: "Error al cargar el juego"
                    }
                }
            }
        }
        viewModel.loadGameDetail(gameId)
    }
    private fun bindGameDetail(gameDetail: GameDetail) {
        binding.tvGameName.text = gameDetail.name
        binding.ivGameImage.load(gameDetail.imageUrl)
        binding.tvDescription.text = gameDetail.description
        binding.tvGameRating.text = String.format(Locale.getDefault(), "%.1f", gameDetail.rating ?: 0.0)
        binding.tvReleaseDate.text = gameDetail.releaseDate ?: ""
        binding.tvGenres.text = gameDetail.genres.joinToString(", ")
        binding.tvPlatforms.text = gameDetail.platforms.joinToString(", ")
        binding.tvPublishers.text = gameDetail.publishers.joinToString(", ")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
