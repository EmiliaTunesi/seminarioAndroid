package ar.edu.unicen.seminario.core.data.model

import com.google.gson.annotations.SerializedName

object FilterDto {
    data class PlatformListResponse(
        @SerializedName("count")
        val count: Int,
        @SerializedName("next")
        val next: String?,
        @SerializedName("previous")
        val previous: String?,
        @SerializedName("results")
        val results: List<PlatformDto>
    )
    data class GenreListResponse(
        @SerializedName("count")
        val count: Int,
        @SerializedName("next")
        val next: String?,
        @SerializedName("previous")
        val previous: String?,
        @SerializedName("results")
        val results: List<GenreDto>
    )
    data class PublisherListResponse(
        @SerializedName("count")
        val count: Int,
        @SerializedName("next")
        val next: String?,
        @SerializedName("previous")
        val previous: String?,
        @SerializedName("results")
        val results: List<PublisherDto>
    )
    data class StoreListResponse(
        @SerializedName("count")
        val count: Int,
        @SerializedName("next")
        val next: String?,
        @SerializedName("previous")
        val previous: String?,
        @SerializedName("results")
        val results: List<StoreDto>
    )

    data class PlatformDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("slug")
        val slug: String,
        @SerializedName("games_count")
        val gamesCount: Int?
    )
    data class GenreDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("slug")
        val slug: String,
        @SerializedName("games_count")
        val gamesCount: Int?
    )
    data class PublisherDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("slug")
        val slug: String,
        @SerializedName("games_count")
        val gamesCount: Int?
    )
    data class StoreDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("slug")
        val slug: String,
        @SerializedName("games_count")
        val gamesCount: Int?
    )
}
