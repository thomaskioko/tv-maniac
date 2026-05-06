package com.thomaskioko.tvmaniac.core.base.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Test fake for [AppScopeLauncher] that runs launched blocks on a caller-supplied scope.
 *
 * Pass the test's `TestScope` as [scope] so launched work executes under the same
 * scheduler as the test body and `runTest` can advance it to completion.
 */
public class FakeAppScopeLauncher(
    private val scope: CoroutineScope,
) : AppScopeLauncher {

    private val recordedTags = mutableListOf<String>()

    public val launches: List<String> get() = recordedTags.toList()

    override fun launch(
        tag: String,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        recordedTags += tag
        return scope.launch { block() }
    }
}
