package com.thomaskioko.tvmaniac.db

import kotlin.jvm.JvmInline

@JvmInline
public value class CastId(public val traktId: Long)

@JvmInline
public value class EpisodeId(public val traktId: Long)

@JvmInline
public value class EpisodeImageId(public val traktId: Long)

@JvmInline
public value class GenreId(public val id: Long)

@JvmInline
public value class NetworkId(public val id: Long)

@JvmInline
public value class PageId(public val id: Long)

@JvmInline
public value class RecommendedShowId(public val id: Long)

@JvmInline
public value class SeasonId(public val id: Long)

@JvmInline
public value class SimilarShowId(public val id: Long)

@JvmInline
public value class TmdbId(public val id: Long)

@JvmInline
public value class WatchProviderId(public val id: Long)

@JvmInline
public value class Id<out T>(public val id: Long)
