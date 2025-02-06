package com.thomaskioko.tvmaniac.presentation.showdetails.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ShowDetails(
  val tmdbId: Long,
  val title: String,
  val overview: String,
  val language: String?,
  val posterImageUrl: String?,
  val backdropImageUrl: String?,
  val year: String,
  val status: String?,
  val votes: Long = 0,
  val numberOfSeasons: Int? = null,
  val numberOfEpisodes: Long? = null,
  val rating: Double,
  val genres: ImmutableList<String>,
  val isFollowed: Boolean,
)
