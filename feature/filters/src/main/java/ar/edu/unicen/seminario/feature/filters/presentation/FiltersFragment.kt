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
import ar.edu.unicen.seminario.core.data.model.domain.GameFilters
import ar.edu.unicen.seminario.feature.filters.databinding.FragmentFiltersBinding
import ar.edu.unicen.seminario.feature.filters.presentation.adapter.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FiltersViewModel by viewModels()

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
        observeSectionStates()
        observeViewModel()
        observeHasActiveFilters()
        arguments?.let { args ->
            @Suppress("DEPRECATION")
            val filters = args.getParcelable<ar.edu.unicen.seminario.core.data.model.domain.GameFilters>("filters")
            if (filters != null) {
                viewModel.setInitialFilters(filters)
            }
        }
    }

    private fun observeHasActiveFilters() {
        lifecycleScope.launch {
            viewModel.hasActiveFiltersFlow.collect { hasActive ->
                binding.btnClearFilters.isEnabled = hasActive
                binding.btnClearFilters.alpha = if (hasActive) 1.0f else 0.5f
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerViews() {
        platformAdapter = PlatformFilterAdapter { platformId ->
            viewModel.togglePlatform(platformId)
        }
        binding.rvPlatforms.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = platformAdapter
        }

        genreAdapter = GenreFilterAdapter { genreId ->
            viewModel.toggleGenre(genreId)
        }
        binding.rvGenres.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = genreAdapter
        }

        publisherAdapter = PublisherFilterAdapter { publisherId ->
            viewModel.togglePublisher(publisherId)
        }
        binding.rvPublishers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = publisherAdapter
        }

        storeAdapter = StoreFilterAdapter { storeId ->
            viewModel.toggleStore(storeId)
        }
        binding.rvStores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storeAdapter
        }
    }

    private fun setupSpinner() {
        val orderingOptions = OrderingType.entries.map { it.displayName }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            orderingOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOrdering.adapter = adapter

        binding.spinnerOrdering.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOrdering = OrderingType.entries[position]
                viewModel.setOrdering(selectedOrdering)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupButtons() {
        binding.btnApplyFilters.setOnClickListener {
            val filters = viewModel.getCurrentFilters()
            val result = Bundle().apply {
                putParcelable("filters", filters)
            }
            parentFragmentManager.setFragmentResult("filters_applied", result)
            findNavController().navigateUp()
        }
        binding.btnClearFilters.setOnClickListener {
            // Clear the ViewModel state, send default (empty) filters back to the list and navigate up
            viewModel.clearAllFilters()
            val clearedFilters = GameFilters() // default empty filters
            val result = Bundle().apply {
                putParcelable("filters", clearedFilters)
            }
            parentFragmentManager.setFragmentResult("filters_applied", result)
            findNavController().navigateUp()
        }
    }

    private fun observeSectionStates() {
        lifecycleScope.launch {
            viewModel.platformsState.collect { state ->
                binding.progressBarPlatforms.isVisible = state.loading
                binding.tvErrorPlatforms.isVisible = state.error != null
                binding.btnRetryPlatforms.isVisible = state.error != null
                binding.rvPlatforms.isVisible = state.data.isNotEmpty()
                binding.tvErrorPlatforms.text = state.error ?: ""
                platformAdapter.submitList(state.data)
            }
        }
        binding.btnRetryPlatforms.setOnClickListener { viewModel.loadPlatforms() }

        lifecycleScope.launch {
            viewModel.genresState.collect { state ->
                binding.progressBarGenres.isVisible = state.loading
                binding.tvErrorGenres.isVisible = state.error != null
                binding.btnRetryGenres.isVisible = state.error != null
                binding.rvGenres.isVisible = state.data.isNotEmpty()
                binding.tvErrorGenres.text = state.error ?: ""
                genreAdapter.submitList(state.data)
            }
        }
        binding.btnRetryGenres.setOnClickListener { viewModel.loadGenres() }

        lifecycleScope.launch {
            viewModel.publishersState.collect { state ->
                binding.progressBarPublishers.isVisible = state.loading
                binding.tvErrorPublishers.isVisible = state.error != null
                binding.btnRetryPublishers.isVisible = state.error != null
                binding.rvPublishers.isVisible = state.data.isNotEmpty()
                binding.tvErrorPublishers.text = state.error ?: ""
                publisherAdapter.submitList(state.data)
            }
        }
        binding.btnRetryPublishers.setOnClickListener { viewModel.loadPublishers() }

        lifecycleScope.launch {
            viewModel.storesState.collect { state ->
                binding.progressBarStores.isVisible = state.loading
                binding.tvErrorStores.isVisible = state.error != null
                binding.btnRetryStores.isVisible = state.error != null
                binding.rvStores.isVisible = state.data.isNotEmpty()
                binding.tvErrorStores.text = state.error ?: ""
                storeAdapter.submitList(state.data)
            }
        }
        binding.btnRetryStores.setOnClickListener { viewModel.loadStores() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.selectedPlatforms.collect { selectedIds ->
                platformAdapter.updateSelectedIds(selectedIds)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedGenres.collect { selectedIds ->
                genreAdapter.updateSelectedIds(selectedIds)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedPublishers.collect { selectedIds ->
                publisherAdapter.updateSelectedIds(selectedIds)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedStores.collect { selectedIds ->
                storeAdapter.updateSelectedIds(selectedIds)
            }
        }

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
