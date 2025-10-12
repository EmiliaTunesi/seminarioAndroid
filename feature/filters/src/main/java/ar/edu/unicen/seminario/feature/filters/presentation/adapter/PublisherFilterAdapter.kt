package ar.edu.unicen.seminario.feature.filters.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unicen.seminario.core.data.model.domain.Publisher
import ar.edu.unicen.seminario.feature.filters.databinding.ItemFilterCheckboxBinding

/**
 * Adaptador para mostrar publishers como checkboxes seleccionables
 */
class PublisherFilterAdapter(
    private val onPublisherToggle: (Int) -> Unit
) : ListAdapter<Publisher, PublisherFilterAdapter.PublisherViewHolder>(DIFF_CALLBACK) {

    private var selectedIds: Set<Int> = emptySet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublisherViewHolder {
        val binding = ItemFilterCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PublisherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PublisherViewHolder, position: Int) {
        val publisher = getItem(position)
        holder.bind(publisher, selectedIds.contains(publisher.id))
    }

    fun updateSelectedIds(selectedIds: Set<Int>) {
        this.selectedIds = selectedIds
        notifyDataSetChanged()
    }

    inner class PublisherViewHolder(
        private val binding: ItemFilterCheckboxBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.checkbox.setOnCheckedChangeListener { _, _ ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val publisher = getItem(position)
                    onPublisherToggle(publisher.id)
                }
            }
        }

        fun bind(publisher: Publisher, isSelected: Boolean) {
            binding.apply {
                checkbox.text = publisher.name
                checkbox.isChecked = isSelected
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Publisher>() {
            override fun areItemsTheSame(oldItem: Publisher, newItem: Publisher): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Publisher, newItem: Publisher): Boolean {
                return oldItem == newItem
            }
        }
    }
}
