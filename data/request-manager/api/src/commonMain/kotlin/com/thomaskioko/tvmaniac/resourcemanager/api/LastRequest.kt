package com.thomaskioko.tvmaniac.resourcemanager.api

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class LastRequest(
  val id: Long = 0,
  val entityId: Long,
  val requestType: String,
  val timestamp: Instant = Clock.System.now(),
)
