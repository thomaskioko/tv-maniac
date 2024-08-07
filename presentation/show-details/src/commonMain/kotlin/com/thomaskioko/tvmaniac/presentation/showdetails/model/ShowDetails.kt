package com.thomaskioko.tvmaniac.presentation.showdetails.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ShowDetails(
  val tmdbId: Long = 0,
  val title: String = "",
  val overview: String = "",
  val language: String? = null,
  val posterImageUrl: String? = null,
  val backdropImageUrl: String? = null,
  val year: String = "",
  val status: String? = null,
  val votes: Long = 0,
  val numberOfSeasons: Int? = null,
  val numberOfEpisodes: Long? = null,
  val rating: Double = 0.0,
  val genres: ImmutableList<String> = persistentListOf(),
  val isFollowed: Boolean = false,
)
