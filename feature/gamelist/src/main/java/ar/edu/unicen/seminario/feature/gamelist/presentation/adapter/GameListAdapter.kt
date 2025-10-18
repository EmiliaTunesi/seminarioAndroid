package ar.edu.unicen.seminario.feature.gamelist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ar.edu.unicen.seminario.core.data.model.domain.Game
import ar.edu.unicen.seminario.feature.gamelist.databinding.ItemGameBinding

class GameListAdapter(
    private val onGameClick: (Game) -> Unit
) : PagingDataAdapter<Game, GameListAdapter.GameViewHolder>(GameDiffCallback()) {
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = getItem(position)
        if (game != null) {
            holder.bind(game, onGameClick)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    class GameViewHolder(
        private val binding: ItemGameBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game, onGameClick: (Game) -> Unit) {
            binding.root.setOnClickListener { onGameClick(game) }
            binding.tvGameName.text = game.name
            binding.ivGameImage.load(game.imageUrl)
            binding.tvGameRating.text = game.rating?.toString() ?: ""
            binding.tvGameGenres.text = game.genres.joinToString(", ")
            binding.tvGameReleaseDate.text = game.releaseDate ?: ""
        }
    }

    class GameDiffCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }
}
