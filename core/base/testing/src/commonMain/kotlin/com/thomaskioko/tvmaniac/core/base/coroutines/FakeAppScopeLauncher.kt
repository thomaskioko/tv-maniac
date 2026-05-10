package com.thomaskioko.tvmaniac.core.base.coroutines

import kotlinx.coroutines.CancellationException
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
    private val recordedThrowables = mutableListOf<Throwable>()

    public val launches: List<String> get() = recordedTags.toList()

    public val thrown: List<Throwable> get() = recordedThrowables.toList()

    override fun launch(
        tag: String,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        recordedTags += tag
        return scope.launch {
            try {
                block()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                recordedThrowables += throwable
            }
        }
    }
}
