package com.thomaskioko.tvmaniac.base.model

import kotlinx.coroutines.CoroutineScope

data class AppCoroutineScope(
    val default: CoroutineScope,
    val io: CoroutineScope,
    val main: CoroutineScope,
)