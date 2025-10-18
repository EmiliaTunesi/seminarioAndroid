package ar.edu.unicen.seminario.core.data.model

import com.google.gson.annotations.SerializedName

object GameDto {
    data class GameListResponse(
        @SerializedName("count")
        val count: Int,
        @SerializedName("next")
        val next: String?,
        @SerializedName("previous")
        val previous: String?,
        @SerializedName("results")
        val results: List<GameResponse>
    )

    data class GameResponse(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("background_image")
        val backgroundImage: String?,
        @SerializedName("rating")
        val rating: Double?,
        @SerializedName("rating_top")
        val ratingTop: Int?,
        @SerializedName("ratings_count")
        val ratingsCount: Int?,
        @SerializedName("metacritic")
        val metacritic: Int?,
        @SerializedName("released")
        val released: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("genres")
        val genres: List<GenreDto>?,
        @SerializedName("platforms")
        val platforms: List<PlatformWrapperDto>?,
        @SerializedName("publishers")
        val publishers: List<PublisherDto>?,
        @SerializedName("stores")
        val stores: List<StoreWrapperDto>?,
        @SerializedName("tags")
        val tags: List<TagDto>?
    )

    data class GameDetailResponse(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("background_image")
        val backgroundImage: String?,
        @SerializedName("rating")
        val rating: Double?,
        @SerializedName("rating_top")
        val ratingTop: Int?,
        @SerializedName("ratings_count")
        val ratingsCount: Int?,
        @SerializedName("metacritic")
        val metacritic: Int?,
        @SerializedName("released")
        val released: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("description_raw")
        val descriptionRaw: String?,
        @SerializedName("genres")
        val genres: List<GenreDto>?,
        @SerializedName("platforms")
        val platforms: List<PlatformWrapperDto>?,
        @SerializedName("publishers")
        val publishers: List<PublisherDto>?,
        @SerializedName("stores")
        val stores: List<StoreWrapperDto>?,
        @SerializedName("tags")
        val tags: List<TagDto>?,
        @SerializedName("website")
        val website: String?,
        @SerializedName("playtime")
        val playtime: Int?
    )

    data class GenreDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("slug")
        val slug: String
    )

    data class PlatformWrapperDto(
        @SerializedName("platform")
        val platform: PlatformDto
    )

    data class PlatformDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("slug")
        val slug: String
    )

    data class PublisherDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("slug")
        val slug: String
    )

    data class StoreWrapperDto(
        @SerializedName("store")
        val store: StoreDto
    )

    data class StoreDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("slug")
        val slug: String
    )

    data class TagDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("slug")
        val slug: String
    )
}
