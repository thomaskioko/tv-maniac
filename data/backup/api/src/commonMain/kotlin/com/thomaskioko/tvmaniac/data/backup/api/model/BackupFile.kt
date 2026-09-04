package com.thomaskioko.tvmaniac.data.backup.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class BackupFile(
    val version: Int,
    val createdAt: String,
    val appVersion: String,
    val shows: List<BackupShow> = emptyList(),
    val lists: List<BackupList> = emptyList(),
    val preferences: BackupPreferences = BackupPreferences(),
)

@Serializable
public data class BackupList(
    val name: String,
    val description: String? = null,
    val createdAt: String? = null,
    val shows: List<BackupListShow> = emptyList(),
)

@Serializable
public data class BackupListShow(
    val tmdbId: Long,
    val listedAt: String,
)

@Serializable
public data class BackupShow(
    val tmdbId: Long,
    val title: String,
    val overview: String? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val year: String? = null,
    val language: String? = null,
    val status: String? = null,
    val runtime: Long? = null,
    val ratings: Double? = null,
    val voteCount: Long? = null,
    val genres: List<String> = emptyList(),
    val seasonNumbers: String? = null,
    val episodeNumbers: String? = null,
    val seasons: List<BackupSeason> = emptyList(),
    val followedAt: Long? = null,
    val watchStatus: String? = null,
    val rating: BackupRating? = null,
    val watchedEpisodes: List<BackupWatchedEpisode> = emptyList(),
    val seasonRatings: List<BackupSeasonRating> = emptyList(),
    val episodeRatings: List<BackupEpisodeRating> = emptyList(),
)

@Serializable
public data class BackupSeason(
    val tmdbId: Long,
    val seasonNumber: Long,
    val title: String,
    val episodeCount: Long,
    val overview: String? = null,
    val imageUrl: String? = null,
    val episodes: List<BackupEpisode> = emptyList(),
)

@Serializable
public data class BackupEpisode(
    val tmdbId: Long,
    val episodeNumber: Long,
    val title: String,
    val overview: String? = null,
    val runtime: Long? = null,
    val voteCount: Long? = null,
    val ratings: Double? = null,
    val imageUrl: String? = null,
    val firstAired: Long? = null,
)

@Serializable
public data class BackupWatchedEpisode(
    val season: Long,
    val episode: Long,
    val watchedAt: Long,
)

@Serializable
public data class BackupRating(
    val value: Long,
    val ratedAt: Long? = null,
)

@Serializable
public data class BackupSeasonRating(
    val season: Long,
    val value: Long,
    val ratedAt: Long? = null,
)

@Serializable
public data class BackupEpisodeRating(
    val season: Long,
    val episode: Long,
    val value: Long,
    val ratedAt: Long? = null,
)

@Serializable
public data class BackupPreferences(
    @SerialName("theme") val theme: String? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("listStyle") val listStyle: String? = null,
    @SerialName("imageQuality") val imageQuality: String? = null,
    @SerialName("openTrailersInYoutube") val openTrailersInYoutube: Boolean? = null,
    @SerialName("includeSpecials") val includeSpecials: Boolean? = null,
    @SerialName("backgroundSyncEnabled") val backgroundSyncEnabled: Boolean? = null,
    @SerialName("episodeNotificationsEnabled") val episodeNotificationsEnabled: Boolean? = null,
    @SerialName("librarySortOption") val librarySortOption: String? = null,
    @SerialName("upNextSortOption") val upNextSortOption: String? = null,
    @SerialName("watchlistSortOption") val watchlistSortOption: String? = null,
    @SerialName("genreShowCategory") val genreShowCategory: String? = null,
    @SerialName("crashReportingEnabled") val crashReportingEnabled: Boolean? = null,
    @SerialName("hapticFeedbackEnabled") val hapticFeedbackEnabled: Boolean? = null,
    @SerialName("seasonSortOrder") val seasonSortOrder: String? = null,
    @SerialName("blurUnwatchedEpisodeImages") val blurUnwatchedEpisodeImages: Boolean? = null,
    @SerialName("hiddenDiscoverSections") val hiddenDiscoverSections: List<String> = emptyList(),
    @SerialName("fontSizePercent") val fontSizePercent: Int? = null,
    @SerialName("posterWidth") val posterWidth: String? = null,
    @SerialName("landscapeWidth") val landscapeWidth: String? = null,
    @SerialName("posterCornerStyle") val posterCornerStyle: String? = null,
    @SerialName("quickRateEnabled") val quickRateEnabled: Boolean? = null,
    @SerialName("multiplePlaysEnabled") val multiplePlaysEnabled: Boolean? = null,
    @SerialName("customWatchDateEnabled") val customWatchDateEnabled: Boolean? = null,
)
