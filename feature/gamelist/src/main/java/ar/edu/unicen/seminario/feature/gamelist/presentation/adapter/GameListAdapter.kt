package ar.edu.unicen.seminario.feature.gamelist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ar.edu.unicen.seminario.core.data.model.domain.Game
import ar.edu.unicen.seminario.feature.gamelist.databinding.ItemGameBinding

/**
 * Adaptador de RecyclerView que usa PagingDataAdapter de Paging 3
 * Implementa las mejores prácticas de la documentación oficial de Android
 */
class GameListAdapter(
    private val onGameClick: (Game) -> Unit
) : PagingDataAdapter<Game, GameListAdapter.GameViewHolder>(GAME_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = getItem(position)
        if (game != null) {
            holder.bind(game)
        }
    }

    inner class GameViewHolder(
        private val binding: ItemGameBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Configurar el click listener para toda la tarjeta
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val game = getItem(position)
                    if (game != null) {
                        onGameClick(game)
                    }
                }
            }
        }

        fun bind(game: Game) {
            binding.apply {
                // Configurar el nombre del juego
                tvGameName.text = game.name

                // Configurar la imagen del juego usando Coil
                ivGameImage.load(game.imageUrl) {
                    crossfade(true)
                    placeholder(ar.edu.unicen.seminario.core.ui.R.drawable.ic_error)
                    error(ar.edu.unicen.seminario.core.ui.R.drawable.ic_error)
                }

                // Mostrar el rating si está disponible
                if (game.rating != null && game.rating > 0) {
                    tvGameRating.text = "★ ${String.format("%.1f", game.rating)}"
                    tvGameRating.visibility = android.view.View.VISIBLE
                } else {
                    tvGameRating.visibility = android.view.View.GONE
                }

                // Mostrar los géneros
                if (game.genres.isNotEmpty()) {
                    tvGameGenres.text = game.genres.joinToString(", ")
                    tvGameGenres.visibility = android.view.View.VISIBLE
                } else {
                    tvGameGenres.visibility = android.view.View.GONE
                }

                // Mostrar la fecha de lanzamiento si está disponible
                if (!game.releaseDate.isNullOrEmpty()) {
                    tvGameReleaseDate.text = "Lanzamiento: ${game.releaseDate}"
                    tvGameReleaseDate.visibility = android.view.View.VISIBLE
                } else {
                    tvGameReleaseDate.visibility = android.view.View.GONE
                }
            }
        }
    }

    companion object {
        /**
         * DiffCallback optimizado para Paging 3
         * Crucial para el rendimiento de la lista paginada
         */
        private val GAME_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
                return oldItem == newItem
            }
        }
    }
}
