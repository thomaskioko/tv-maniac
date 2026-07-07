package com.thomaskioko.tvmaniac.episodes.api.model

public data class ShowMetadataSyncInfo(
    public val status: String?,
    public val metadataEpisodeCount: Long,
    public val localEpisodeCount: Long,
)
