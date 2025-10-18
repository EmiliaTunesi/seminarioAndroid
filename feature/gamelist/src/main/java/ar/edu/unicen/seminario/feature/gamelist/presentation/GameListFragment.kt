package ar.edu.unicen.seminario.feature.gamelist.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ar.edu.unicen.seminario.core.data.model.domain.Game
import ar.edu.unicen.seminario.core.data.model.domain.GameFilters
import ar.edu.unicen.seminario.feature.gamelist.R
import ar.edu.unicen.seminario.feature.gamelist.presentation.adapter.GameListAdapter

class GameListFragment : Fragment() {
    private val viewModel: GameListViewModel by viewModels()
    private lateinit var gameListAdapter: GameListAdapter

    // Para compatibilidad con API
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("filters_applied") { _, bundle ->
            val filters = if (android.os.Build.VERSION.SDK_INT >= 33) {
                bundle.getParcelable("filters", GameFilters::class.java)
            } else {
                bundle.getParcelable("filters") as? GameFilters
            }
            if (filters != null) {
                viewModel.applyFilters(filters)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewGames)
        gameListAdapter = GameListAdapter { game: Game -> }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = gameListAdapter
        lifecycleScope.launch {
            viewModel.gamesFlow.collectLatest { pagingData: androidx.paging.PagingData<Game> ->
                gameListAdapter.submitData(pagingData)
            }
        }
    }
}
