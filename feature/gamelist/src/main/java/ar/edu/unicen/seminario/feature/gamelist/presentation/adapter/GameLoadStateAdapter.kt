package ar.edu.unicen.seminario.feature.gamelist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unicen.seminario.feature.gamelist.databinding.ItemLoadStateBinding

class GameLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<GameLoadStateAdapter.LoadStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState, retry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }

    class LoadStateViewHolder(
        private val binding: ItemLoadStateBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState, retry: () -> Unit) {
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.btnRetry.isVisible = loadState is LoadState.Error
            binding.tvErrorMessage.isVisible = loadState is LoadState.Error
            binding.btnRetry.setOnClickListener { retry() }
            binding.tvErrorMessage.text = if (loadState is LoadState.Error) loadState.error.localizedMessage else ""
        }
    }
}
