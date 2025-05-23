package com.thomaskioko.tvmaniac.db

import kotlin.jvm.JvmInline

@JvmInline
value class CastId(val traktId: Long)

@JvmInline
value class EpisodeId(val traktId: Long)

@JvmInline
value class EpisodeImageId(val traktId: Long)

@JvmInline
value class GenreId(val id: Long)

@JvmInline
value class NetworkId(val id: Long)

@JvmInline
value class PageId(val id: Long)

@JvmInline
value class RecommendedShowId(val id: Long)

@JvmInline
value class SeasonId(val id: Long)

@JvmInline
value class SimilarShowId(val id: Long)

@JvmInline
value class TmdbId(val id: Long)

@JvmInline
value class WatchProviderId(val id: Long)

@JvmInline
value class Id<out T>(val id: Long)
