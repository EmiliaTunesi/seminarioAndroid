package ar.edu.unicen.seminario.core.data.model.domain

data class GameDetail(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val rating: Double?,
    val genres: List<String>,
    val releaseDate: String?,
    val description: String?,
    val platforms: List<String>,
    val publishers: List<String>,
    val stores: List<String>,
    val website: String?
)

