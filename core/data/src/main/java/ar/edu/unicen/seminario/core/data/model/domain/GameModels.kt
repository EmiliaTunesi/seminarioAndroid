package ar.edu.unicen.seminario.core.data.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de dominio para representar un juego
 */
@Parcelize
data class Game(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val rating: Double?,
    val genres: List<String>,
    val releaseDate: String?,
    val description: String?,
    val platforms: List<String>,
    val publishers: List<String>,
    val stores: List<String>
) : Parcelable

/**
 * Modelo para representar los filtros de búsqueda de juegos
 */
@Parcelize
data class GameFilters(
    val platforms: List<Int> = emptyList(),
    val genres: List<Int> = emptyList(),
    val publishers: List<Int> = emptyList(),
    val stores: List<Int> = emptyList(),
    val ordering: String = "-added"
) : Parcelable

/**
 * Modelo para representar una plataforma
 */
@Parcelize
data class Platform(
    val id: Int,
    val name: String,
    val slug: String
) : Parcelable

/**
 * Modelo para representar un género
 */
@Parcelize
data class Genre(
    val id: Int,
    val name: String,
    val slug: String
) : Parcelable

/**
 * Modelo para representar un publisher
 */
@Parcelize
data class Publisher(
    val id: Int,
    val name: String,
    val slug: String
) : Parcelable

/**
 * Modelo para representar una tienda
 */
@Parcelize
data class Store(
    val id: Int,
    val name: String,
    val slug: String
) : Parcelable

