package com.thomaskioko.tvmaniac.db

import kotlin.jvm.JvmInline

@JvmInline
value class CategoryId(val id: Long)

@JvmInline
value class EpisodeId(val traktId: Long)

@JvmInline
value class EpisodeImageId(val traktId: Long)

@JvmInline
value class NetworkId(val id: Long)

@JvmInline
value class PageId(val id: Long)

@JvmInline
value class SeasonId(val id: Long)

@JvmInline
value class ShowId(val traktId: Long)

@JvmInline
value class SimilarShowId(val id: Long)

@JvmInline
value class TmdbId(val id: Long)

@JvmInline
value class Id<out T>(val id: Long)
