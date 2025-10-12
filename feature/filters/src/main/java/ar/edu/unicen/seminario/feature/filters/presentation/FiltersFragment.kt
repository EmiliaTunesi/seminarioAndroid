package ar.edu.unicen.seminario.feature.filters.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unicen.seminario.core.data.model.domain.OrderingType
import ar.edu.unicen.seminario.feature.filters.databinding.FragmentFiltersBinding
import ar.edu.unicen.seminario.feature.filters.presentation.adapter.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment que permite al usuario seleccionar filtros para la búsqueda de juegos
 */
@AndroidEntryPoint
class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FiltersViewModel by viewModels()

    // Adaptadores para las listas de filtros
    private lateinit var platformAdapter: PlatformFilterAdapter
    private lateinit var genreAdapter: GenreFilterAdapter
    private lateinit var publisherAdapter: PublisherFilterAdapter
    private lateinit var storeAdapter: StoreFilterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerViews()
        setupSpinner()
        setupButtons()
        observeViewModel()

        // Cargar filtros iniciales si los hay (viene de argumentos de navegación)
        arguments?.let { args ->
            val filters = args.getParcelable<ar.edu.unicen.seminario.core.data.model.domain.GameFilters>("filters")
            if (filters != null) {
                viewModel.setInitialFilters(filters)
            }
        }
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
     * Configura todos los RecyclerViews con sus adaptadores correspondientes
     */
    private fun setupRecyclerViews() {
        // Adaptador de plataformas
        platformAdapter = PlatformFilterAdapter { platformId ->
            viewModel.togglePlatform(platformId)
        }
        binding.rvPlatforms.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = platformAdapter
        }

        // Adaptador de géneros
        genreAdapter = GenreFilterAdapter { genreId ->
            viewModel.toggleGenre(genreId)
        }
        binding.rvGenres.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = genreAdapter
        }

        // Adaptador de publishers
        publisherAdapter = PublisherFilterAdapter { publisherId ->
            viewModel.togglePublisher(publisherId)
        }
        binding.rvPublishers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = publisherAdapter
        }

        // Adaptador de tiendas
        storeAdapter = StoreFilterAdapter { storeId ->
            viewModel.toggleStore(storeId)
        }
        binding.rvStores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storeAdapter
        }
    }

    /**
     * Configura el spinner de ordenamiento
     */
    private fun setupSpinner() {
        val orderingOptions = OrderingType.values().map { it.displayName }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            orderingOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOrdering.adapter = adapter

        binding.spinnerOrdering.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOrdering = OrderingType.values()[position]
                viewModel.setOrdering(selectedOrdering)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    /**
     * Configura los botones de acción
     */
    private fun setupButtons() {
        // Botón para limpiar filtros
        binding.btnClearFilters.setOnClickListener {
            viewModel.clearAllFilters()
            // Resetear el spinner al valor por defecto
            binding.spinnerOrdering.setSelection(OrderingType.ADDED_DESC.ordinal)
        }

        // Botón para aplicar filtros
        binding.btnApplyFilters.setOnClickListener {
            val filters = viewModel.getCurrentFilters()
            // Navegar de vuelta a la lista con los filtros aplicados
            val bundle = Bundle().apply {
                putParcelable("filters", filters)
            }
            findNavController().navigate(
                ar.edu.unicen.seminario.core.navigation.NavigationRoutes.GAME_LIST,
                bundle
            )
        }

        // Botón de reintentar en caso de error
        binding.errorLayout.btnRetry.setOnClickListener {
            viewModel.loadFiltersData()
        }
    }

    /**
     * Observa los estados del ViewModel y actualiza la UI
     */
    private fun observeViewModel() {
        // Observar el estado de la UI
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is FiltersUiState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.contentLayout.isVisible = false
                        binding.errorLayout.root.isVisible = false
                    }
                    is FiltersUiState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.contentLayout.isVisible = true
                        binding.errorLayout.root.isVisible = false

                        // Actualizar las listas de filtros
                        platformAdapter.submitList(state.platforms)
                        genreAdapter.submitList(state.genres)
                        publisherAdapter.submitList(state.publishers)
                        storeAdapter.submitList(state.stores)
                    }
                    is FiltersUiState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.contentLayout.isVisible = false
                        binding.errorLayout.root.isVisible = true
                        binding.errorLayout.tvErrorMessage.text = state.message
                    }
                }
            }
        }

        // Observar las plataformas seleccionadas
        lifecycleScope.launch {
            viewModel.selectedPlatforms.collect { selectedIds ->
                platformAdapter.updateSelectedIds(selectedIds)
            }
        }

        // Observar los géneros seleccionados
        lifecycleScope.launch {
            viewModel.selectedGenres.collect { selectedIds ->
                genreAdapter.updateSelectedIds(selectedIds)
            }
        }

        // Observar los publishers seleccionados
        lifecycleScope.launch {
            viewModel.selectedPublishers.collect { selectedIds ->
                publisherAdapter.updateSelectedIds(selectedIds)
            }
        }

        // Observar las tiendas seleccionadas
        lifecycleScope.launch {
            viewModel.selectedStores.collect { selectedIds ->
                storeAdapter.updateSelectedIds(selectedIds)
            }
        }

        // Observar el ordenamiento seleccionado
        lifecycleScope.launch {
            viewModel.selectedOrdering.collect { orderingType ->
                binding.spinnerOrdering.setSelection(orderingType.ordinal, false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
