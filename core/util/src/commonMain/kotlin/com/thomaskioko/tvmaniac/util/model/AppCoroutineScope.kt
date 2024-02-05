package com.thomaskioko.tvmaniac.util.model

import kotlinx.coroutines.CoroutineScope

data class AppCoroutineScope(
  val default: CoroutineScope,
  val io: CoroutineScope,
  val main: CoroutineScope,
)
