package com.thomaskioko.tvmaniac.domain.statistics.model

import com.thomaskioko.tvmaniac.db.WatchStatus

public data class WatchStatusCount(
    val status: WatchStatus,
    val showCount: Long,
)
