package com.thomaskioko.tvmaniac.core.base.model

import kotlinx.coroutines.CoroutineScope

data class AppCoroutineScope(
  val default: CoroutineScope,
  val io: CoroutineScope,
  val main: CoroutineScope,
)
