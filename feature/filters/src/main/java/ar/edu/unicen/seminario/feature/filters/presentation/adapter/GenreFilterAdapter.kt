package ar.edu.unicen.seminario.feature.filters.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unicen.seminario.core.data.model.domain.Genre
import ar.edu.unicen.seminario.feature.filters.databinding.ItemFilterCheckboxBinding

/**
 * Adaptador para mostrar géneros como checkboxes seleccionables
 */
class GenreFilterAdapter(
    private val onGenreToggle: (Int) -> Unit
) : ListAdapter<Genre, GenreFilterAdapter.GenreViewHolder>(DIFF_CALLBACK) {

    private var selectedIds: Set<Int> = emptySet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val binding = ItemFilterCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = getItem(position)
        holder.bind(genre, selectedIds.contains(genre.id))
    }

    fun updateSelectedIds(selectedIds: Set<Int>) {
        this.selectedIds = selectedIds
        notifyDataSetChanged()
    }

    inner class GenreViewHolder(
        private val binding: ItemFilterCheckboxBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.checkbox.setOnCheckedChangeListener { _, _ ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val genre = getItem(position)
                    onGenreToggle(genre.id)
                }
            }
        }

        fun bind(genre: Genre, isSelected: Boolean) {
            binding.apply {
                // Quitar el listener antes de modificar el estado
                checkbox.setOnCheckedChangeListener(null)
                tvFilterName.text = genre.name
                checkbox.isChecked = isSelected
                checkbox.text = ""
                // Volver a setear el listener
                checkbox.setOnCheckedChangeListener { _, _ ->
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val genre = getItem(position)
                        onGenreToggle(genre.id)
                    }
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Genre>() {
            override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
                return oldItem == newItem
            }
        }
    }
}
