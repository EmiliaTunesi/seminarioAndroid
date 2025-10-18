package ar.edu.unicen.seminario.feature.filters.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unicen.seminario.core.data.model.domain.Platform
import ar.edu.unicen.seminario.feature.filters.databinding.ItemFilterCheckboxBinding

/**
 * Adaptador para mostrar plataformas como checkboxes seleccionables
 */
class PlatformFilterAdapter(
    private val onPlatformToggle: (Int) -> Unit
) : ListAdapter<Platform, PlatformFilterAdapter.PlatformViewHolder>(DIFF_CALLBACK) {

    private var selectedIds: Set<Int> = emptySet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformViewHolder {
        val binding = ItemFilterCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlatformViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlatformViewHolder, position: Int) {
        val platform = getItem(position)
        holder.bind(platform, selectedIds.contains(platform.id))
    }

    fun updateSelectedIds(selectedIds: Set<Int>) {
        this.selectedIds = selectedIds
        notifyDataSetChanged()
    }

    inner class PlatformViewHolder(
        private val binding: ItemFilterCheckboxBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.checkbox.setOnCheckedChangeListener { _, _ ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val platform = getItem(position)
                    onPlatformToggle(platform.id)
                }
            }
        }

        fun bind(platform: Platform, isSelected: Boolean) {
            binding.apply {
                // Quitar el listener antes de modificar el estado
                checkbox.setOnCheckedChangeListener(null)
                tvFilterName.text = platform.name
                checkbox.isChecked = isSelected
                checkbox.text = ""
                // Volver a setear el listener
                checkbox.setOnCheckedChangeListener { _, _ ->
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val platform = getItem(position)
                        onPlatformToggle(platform.id)
                    }
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Platform>() {
            override fun areItemsTheSame(oldItem: Platform, newItem: Platform): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Platform, newItem: Platform): Boolean {
                return oldItem == newItem
            }
        }
    }
}
