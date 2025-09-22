package com.thomaskioko.tvmaniac.episodes.api.model

import com.thomaskioko.tvmaniac.db.Watched_episodes

/**
 * Calculates the absolute episode number using the formula: season * 1000 + episode
 * This ensures episodes are correctly ordered across seasons (e.g., S2E1 comes after S1E10)
 */
public fun Long.toAbsoluteEpisodeNumber(episodeNumber: Long): Long =
    this * 1000 + episodeNumber

/**
 * Extension function to get the absolute episode number for a watched episode
 */
public fun Watched_episodes.absoluteEpisodeNumber(): Long =
    season_number.toAbsoluteEpisodeNumber(episode_number)

/**
 * Extension function to get the absolute episode number for any episode with season/episode numbers
 */
public fun calculateAbsoluteEpisodeNumber(seasonNumber: Long, episodeNumber: Long): Long =
    seasonNumber.toAbsoluteEpisodeNumber(episodeNumber)
