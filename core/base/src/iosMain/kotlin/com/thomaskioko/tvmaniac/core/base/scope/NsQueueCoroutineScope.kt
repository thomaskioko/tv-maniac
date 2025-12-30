package com.thomaskioko.tvmaniac.core.base.scope

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

public class NsQueueCoroutineScope : CoroutineScope {

    private val coroutineDispatcher: CoroutineDispatcher = applicationNsQueueDispatcher
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + coroutineDispatcher
}
