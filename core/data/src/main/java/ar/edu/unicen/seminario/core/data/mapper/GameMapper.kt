package ar.edu.unicen.seminario.core.data.mapper

import ar.edu.unicen.seminario.core.data.model.GameDto
import ar.edu.unicen.seminario.core.data.model.FilterDto
import ar.edu.unicen.seminario.core.data.model.domain.Game
import ar.edu.unicen.seminario.core.data.model.domain.Platform
import ar.edu.unicen.seminario.core.data.model.domain.Genre
import ar.edu.unicen.seminario.core.data.model.domain.Publisher
import ar.edu.unicen.seminario.core.data.model.domain.Store
import ar.edu.unicen.seminario.core.data.model.domain.GameDetail

class GameMapper {
    fun mapToGameList(gameDtos: List<GameDto.GameResponse>): List<Game> {
        return gameDtos.map { mapToGame(it) }
    }

    fun mapToGame(gameDto: GameDto.GameResponse): Game {
        return Game(
            id = gameDto.id,
            name = gameDto.name,
            imageUrl = gameDto.backgroundImage,
            rating = gameDto.rating,
            genres = gameDto.genres?.map { it.name } ?: emptyList(),
            releaseDate = gameDto.released,
            description = gameDto.description,
            platforms = gameDto.platforms?.map { it.platform.name } ?: emptyList(),
            publishers = gameDto.publishers?.map { it.name } ?: emptyList(),
            stores = gameDto.stores?.map { it.store.name } ?: emptyList()
        )
    }

    fun mapToGameDetail(gameDetailDto: GameDto.GameDetailResponse): GameDetail {
        return GameDetail(
            id = gameDetailDto.id,
            name = gameDetailDto.name,
            imageUrl = gameDetailDto.backgroundImage,
            rating = gameDetailDto.rating,
            genres = gameDetailDto.genres?.map { it.name } ?: emptyList(),
            releaseDate = gameDetailDto.released,
            description = gameDetailDto.description,
            platforms = gameDetailDto.platforms?.map { it.platform.name } ?: emptyList(),
            publishers = gameDetailDto.publishers?.map { it.name } ?: emptyList(),
            stores = gameDetailDto.stores?.map { it.store.name } ?: emptyList(),
            website = gameDetailDto.website
        )
    }

    fun mapToPlatformList(platformDtos: List<FilterDto.PlatformDto>): List<Platform> {
        return platformDtos.map {
            Platform(
                id = it.id,
                name = it.name,
                slug = it.slug
            )
        }
    }

    fun mapToGenreList(genreDtos: List<FilterDto.GenreDto>): List<Genre> {
        return genreDtos.map {
            Genre(
                id = it.id,
                name = it.name,
                slug = it.slug
            )
        }
    }

    fun mapToPublisherList(publisherDtos: List<FilterDto.PublisherDto>): List<Publisher> {
        return publisherDtos.map {
            Publisher(
                id = it.id,
                name = it.name,
                slug = it.slug
            )
        }
    }

    fun mapToStoreList(storeDtos: List<FilterDto.StoreDto>): List<Store> {
        return storeDtos.map {
            Store(
                id = it.id,
                name = it.name,
                slug = it.slug
            )
        }
    }
}
