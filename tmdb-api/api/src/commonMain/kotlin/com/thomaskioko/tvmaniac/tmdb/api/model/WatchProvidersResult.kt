package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchProvidersResult(
  @SerialName("id") var id: Int,
  @SerialName("results") var results: Results,
)

@Serializable
data class Results(
  @SerialName("US") var US: US? = US(),
)

@Serializable
data class US(
  @SerialName("link") var link: String? = null,
  @SerialName("flatrate") var flatrate: ArrayList<FlatRate> = arrayListOf(),
  @SerialName("ads") var ads: ArrayList<Ads> = arrayListOf(),
  @SerialName("free") var free: ArrayList<Free> = arrayListOf(),
)

@Serializable
data class FlatRate(
  @SerialName("logo_path") var logoPath: String? = null,
  @SerialName("provider_id") var providerId: Int,
  @SerialName("provider_name") var providerName: String,
  @SerialName("display_priority") var displayPriority: Int? = null,
)

@Serializable
data class Ads(
  @SerialName("logo_path") var logoPath: String? = null,
  @SerialName("provider_id") var providerId: Int,
  @SerialName("provider_name") var providerName: String,
  @SerialName("display_priority") var displayPriority: Int? = null,
)

@Serializable
data class Free(
  @SerialName("logo_path") var logoPath: String? = null,
  @SerialName("provider_id") var providerId: Int,
  @SerialName("provider_name") var providerName: String,
  @SerialName("display_priority") var displayPriority: Int? = null,
)
