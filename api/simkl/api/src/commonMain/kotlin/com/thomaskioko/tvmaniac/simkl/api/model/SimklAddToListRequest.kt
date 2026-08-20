package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklAddToListRequest(
    @SerialName("shows") val shows: List<SimklListShow>,
)

@Serializable
public data class SimklListShow(
    @SerialName("ids") val ids: SimklShowIds,
    @SerialName("to") val to: String,
)

@Serializable
public data class SimklAddToListResponse(
    @SerialName("not_found") val notFound: SimklNotFoundBucket? = null,
)

public object SimklListStatus {
    public const val PLAN_TO_WATCH: String = "plantowatch"
    public const val DROPPED: String = "dropped"
}
