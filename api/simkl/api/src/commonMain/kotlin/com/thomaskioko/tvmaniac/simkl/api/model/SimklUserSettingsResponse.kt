package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklUserSettingsResponse(
    @SerialName("user") val user: SimklUser,
    @SerialName("account") val account: SimklAccount,
)

@Serializable
public data class SimklUser(
    @SerialName("name") val name: String? = null,
    @SerialName("avatar") val avatar: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("gender") val gender: String? = null,
)

@Serializable
public data class SimklAccount(
    @SerialName("id") val id: Long,
)
