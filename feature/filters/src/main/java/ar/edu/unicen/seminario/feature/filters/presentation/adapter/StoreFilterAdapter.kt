package ar.edu.unicen.seminario.feature.filters.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unicen.seminario.core.data.model.domain.Store
import ar.edu.unicen.seminario.feature.filters.databinding.ItemFilterCheckboxBinding

/**
 * Adaptador para mostrar tiendas como checkboxes seleccionables
 */
class StoreFilterAdapter(
    private val onStoreToggle: (Int) -> Unit
) : ListAdapter<Store, StoreFilterAdapter.StoreViewHolder>(DIFF_CALLBACK) {

    private var selectedIds: Set<Int> = emptySet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemFilterCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = getItem(position)
        holder.bind(store, selectedIds.contains(store.id))
    }

    fun updateSelectedIds(selectedIds: Set<Int>) {
        this.selectedIds = selectedIds
        notifyDataSetChanged()
    }

    inner class StoreViewHolder(
        private val binding: ItemFilterCheckboxBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.checkbox.setOnCheckedChangeListener { _, _ ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val store = getItem(position)
                    onStoreToggle(store.id)
                }
            }
        }

        fun bind(store: Store, isSelected: Boolean) {
            binding.apply {
                checkbox.text = store.name
                checkbox.isChecked = isSelected
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Store>() {
            override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
                return oldItem == newItem
            }
        }
    }
}
