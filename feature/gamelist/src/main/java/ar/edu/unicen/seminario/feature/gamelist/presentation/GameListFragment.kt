package ar.edu.unicen.seminario.feature.gamelist.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unicen.seminario.core.data.model.domain.GameFilters
import ar.edu.unicen.seminario.core.navigation.NavigationRoutes
import ar.edu.unicen.seminario.feature.gamelist.databinding.FragmentGameListBinding
import ar.edu.unicen.seminario.feature.gamelist.presentation.adapter.GameListAdapter
import ar.edu.unicen.seminario.feature.gamelist.presentation.adapter.GameLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment que muestra la lista principal de videojuegos con paginación
 */
@AndroidEntryPoint
class GameListFragment : Fragment() {

    private var _binding: FragmentGameListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameListViewModel by viewModels()

    private lateinit var gameAdapter: GameListAdapter
    private lateinit var loadStateAdapter: GameLoadStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupFab()
        observeGamesList()

        // Manejar los argumentos de navegación (filtros aplicados)
        arguments?.let { args ->
            val filters = args.getParcelable<GameFilters>("filters")
            if (filters != null) {
                viewModel.applyFilters(filters)
            }
        }
    }

    /**
     * Configura el RecyclerView con los adaptadores de Paging 3
     * Implementación completa según la documentación oficial
     */
    private fun setupRecyclerView() {
        // Configurar el adaptador principal
        gameAdapter = GameListAdapter { game ->
            // Navegar al detalle del juego cuando se hace click
            val route = NavigationRoutes.createGameDetailRoute(game.id)
            findNavController().navigate(route)
        }

        // Configurar el adaptador de estados de carga
        loadStateAdapter = GameLoadStateAdapter {
            gameAdapter.retry()
        }

        // Configurar el RecyclerView con ConcatAdapter (característica clave de Paging 3)
        binding.rvGames.apply {
            layoutManager = LinearLayoutManager(requireContext())
            // withLoadStateFooter es esencial para mostrar estados de carga
            adapter = gameAdapter.withLoadStateFooter(
                footer = loadStateAdapter
            )

            // Optimizaciones de rendimiento recomendadas
            setHasFixedSize(true)
            itemAnimator?.changeDuration = 0
        }

        // Observar los estados de carga para UI responsiva (funcionalidad clave de Paging 3)
        lifecycleScope.launch {
            gameAdapter.loadStateFlow.collectLatest { loadState ->
                // Mostrar el indicador de SwipeRefresh solo en refresh inicial
                binding.swipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading

                // Mostrar/ocultar el RecyclerView basado en el estado
                binding.rvGames.isVisible = loadState.source.refresh is LoadState.NotLoading

                // Manejar estados de error en el nivel superior
                if (loadState.refresh is LoadState.Error && gameAdapter.itemCount == 0) {
                    // Mostrar mensaje de error para la carga inicial fallida
                    // (aquí podrías mostrar un estado de error global si lo deseas)
                }
            }
        }
    }

    /**
     * Configura el SwipeRefreshLayout para permitir refrescar la lista
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            gameAdapter.refresh()
        }

        // Configurar colores del indicador de refresh
        binding.swipeRefresh.setColorSchemeResources(
            ar.edu.unicen.seminario.core.ui.R.color.primary_blue,
            ar.edu.unicen.seminario.core.ui.R.color.accent_orange
        )
    }

    /**
     * Configura el FAB para navegar a la pantalla de filtros
     */
    private fun setupFab() {
        binding.fabFilters.setOnClickListener {
            findNavController().navigate(NavigationRoutes.FILTERS)
        }
    }

    /**
     * Observa el flujo de juegos del ViewModel y actualiza la UI
     * Implementación correcta de Paging 3 con collectLatest
     */
    private fun observeGamesList() {
        lifecycleScope.launch {
            viewModel.gamesFlow.collectLatest { pagingData ->
                gameAdapter.submitData(pagingData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
