package ar.edu.unicen.seminario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unicen.seminario.core.navigation.NavigationRoutes
import ar.edu.unicen.seminario.feature.gamelist.databinding.FragmentGameListBinding
import ar.edu.unicen.seminario.feature.gamelist.presentation.GameListViewModel
import ar.edu.unicen.seminario.feature.gamelist.presentation.adapter.GameListAdapter
import ar.edu.unicen.seminario.feature.gamelist.presentation.adapter.GameLoadStateAdapter
import ar.edu.unicen.seminario.core.data.model.domain.GameFilters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameListFragment : Fragment() {
    private var _binding: FragmentGameListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameListViewModel by viewModels()
    private lateinit var gameAdapter: GameListAdapter
    private lateinit var loadStateAdapter: GameLoadStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(ar.edu.unicen.seminario.R.menu.menu_gamelist, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            ar.edu.unicen.seminario.R.id.action_filters -> {
                // Pasar los filtros actuales al abrir la sección de filtros
                val currentFilters = viewModel.filters.value
                val bundle = Bundle().apply {
                    putParcelable("filters", currentFilters)
                }
                findNavController().navigate(ar.edu.unicen.seminario.R.id.action_gameList_to_filters, bundle)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeGamesList()

        parentFragmentManager.setFragmentResultListener("filters_applied", viewLifecycleOwner) { _, bundle ->
            @Suppress("DEPRECATION")
            val filters = bundle.getParcelable<GameFilters>("filters")
            if (filters != null) {
                viewModel.applyFilters(filters)
                binding.recyclerViewGames.scrollToPosition(0)
            }
        }
    }

    private fun setupRecyclerView() {
        gameAdapter = GameListAdapter { game ->
            val bundle = Bundle().apply {
                putInt(NavigationRoutes.ARG_GAME_ID, game.id)
            }
            android.util.Log.d("GameListFragment", "Navegando al detalle con gameId: ${game.id}")
            findNavController().navigate(ar.edu.unicen.seminario.R.id.gameDetailFragment, bundle)
        }
        loadStateAdapter = GameLoadStateAdapter { gameAdapter.retry() }
        binding.recyclerViewGames.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = gameAdapter.withLoadStateFooter(footer = loadStateAdapter)
            setHasFixedSize(true)
        }
    }

    private fun setupFab() {
        binding.fabFilters.setOnClickListener {
            // Pasar los filtros actuales al abrir la sección de filtros
            val currentFilters = viewModel.filters.value
            val bundle = Bundle().apply {
                putParcelable("filters", currentFilters)
            }
            findNavController().navigate(ar.edu.unicen.seminario.R.id.action_gameList_to_filters, bundle)
        }
    }

    private fun observeGamesList() {
        (activity as? MainActivity)?.showLoading()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.gamesFlow.collectLatest { pagingData ->
                gameAdapter.submitData(pagingData)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            gameAdapter.loadStateFlow.collectLatest { loadStates ->
                val isLoading = loadStates.refresh is androidx.paging.LoadState.Loading
                if (isLoading) {
                    (activity as? MainActivity)?.showLoading()
                } else {
                    (activity as? MainActivity)?.hideLoading()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
