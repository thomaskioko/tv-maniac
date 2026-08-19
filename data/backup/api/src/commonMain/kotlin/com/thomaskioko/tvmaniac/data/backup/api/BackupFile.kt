package com.thomaskioko.tvmaniac.data.backup.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class BackupFile(
    val version: Int,
    val createdAt: String,
    val appVersion: String,
    val shows: List<BackupShow> = emptyList(),
    val preferences: BackupPreferences = BackupPreferences(),
)

@Serializable
public data class BackupShow(
    val tmdbId: Long,
    val title: String,
    val followedAt: Long? = null,
    val watchStatus: String? = null,
    val rating: BackupRating? = null,
    val watchedEpisodes: List<BackupWatchedEpisode> = emptyList(),
    val seasonRatings: List<BackupSeasonRating> = emptyList(),
    val episodeRatings: List<BackupEpisodeRating> = emptyList(),
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
)
