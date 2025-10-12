package ar.edu.unicen.seminario.feature.gamelist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unicen.seminario.feature.gamelist.databinding.ItemLoadStateBinding

/**
 * LoadStateAdapter para manejar estados de carga de Paging 3
 * Implementa la funcionalidad recomendada por la documentación oficial
 */
class GameLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<GameLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemLoadStateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(
        private val binding: ItemLoadStateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Configurar el botón de reintentar
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                // Mostrar progress bar solo cuando esté cargando
                progressBar.isVisible = loadState is LoadState.Loading

                // Mostrar botón y mensaje de error solo cuando hay error
                btnRetry.isVisible = loadState is LoadState.Error
                tvErrorMessage.isVisible = loadState is LoadState.Error

                // Configurar mensaje de error específico según el tipo
                if (loadState is LoadState.Error) {
                    tvErrorMessage.text = when {
                        loadState.error.message?.contains("network", ignoreCase = true) == true ||
                        loadState.error.message?.contains("connection", ignoreCase = true) == true -> {
                            "Error de conexión. Verificá tu conexión a internet."
                        }
                        loadState.error.message?.contains("timeout", ignoreCase = true) == true -> {
                            "Timeout de conexión. Intentá nuevamente."
                        }
                        else -> "Ocurrió un error inesperado. Intentá nuevamente."
                    }
                }
            }
        }
    }
}
