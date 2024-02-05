package com.thomaskioko.tvmaniac.util.scope

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class NsQueueCoroutineScope : CoroutineScope {

  private val coroutineDispatcher: CoroutineDispatcher = applicationNsQueueDispatcher
  private val job = Job()

  override val coroutineContext: CoroutineContext
    get() = job + coroutineDispatcher
}
