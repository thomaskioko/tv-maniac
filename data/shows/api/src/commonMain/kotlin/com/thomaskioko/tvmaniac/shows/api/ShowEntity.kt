package com.thomaskioko.tvmaniac.shows.api

data class ShowEntity(
  val id: Long,
  val inLibrary: Boolean,
  val posterPath: String?,
  val title: String,
  val page: Long = 0,
)
