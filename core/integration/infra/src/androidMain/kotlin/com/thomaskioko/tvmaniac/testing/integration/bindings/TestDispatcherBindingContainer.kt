package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.core.base.ComputationCoroutineScope
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.MainCoroutineScope
import com.thomaskioko.tvmaniac.core.base.di.BaseBindingContainer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [BaseBindingContainer::class],
)
public object TestDispatcherBindingContainer {

    private val activeScopes = mutableListOf<CoroutineScope>()

    public fun reset() {
        activeScopes.forEach { it.cancel() }
        activeScopes.clear()
    }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideCoroutineDispatchers(): AppCoroutineDispatchers {
        val testScheduler = Dispatchers.Main.immediate
        return AppCoroutineDispatchers(
            io = testScheduler,
            computation = testScheduler,
            databaseWrite = testScheduler,
            databaseRead = testScheduler,
            main = testScheduler,
        ).also(::ensureAllRolesShareTheTestScheduler)
    }

    private fun ensureAllRolesShareTheTestScheduler(dispatchers: AppCoroutineDispatchers) {
        val expected = dispatchers.main
        val mismatched = listOf(
            "io" to dispatchers.io,
            "computation" to dispatchers.computation,
            "databaseWrite" to dispatchers.databaseWrite,
            "databaseRead" to dispatchers.databaseRead,
        ).filter { (_, dispatcher) -> dispatcher !== expected }
        require(mismatched.isEmpty()) {
            "TestDispatcherBindingContainer: every dispatcher role must reference the same " +
                "instance as `main` (the test scheduler). Mismatched roles: " +
                mismatched.joinToString { it.first } +
                ". Splitting roles onto different dispatchers — even other TestDispatchers — " +
                "disconnects presenter emissions from Compose's recomposition scheduler and " +
                "causes silent test hangs. See class KDoc."
        }
    }

    @Provides
    @IoCoroutineScope
    @SingleIn(AppScope::class)
    public fun provideIoCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatchers.io).also { activeScopes.add(it) }

    @Provides
    @MainCoroutineScope
    @SingleIn(AppScope::class)
    public fun provideMainCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatchers.main).also { activeScopes.add(it) }

    @Provides
    @ComputationCoroutineScope
    @SingleIn(AppScope::class)
    public fun provideComputationCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatchers.computation).also { activeScopes.add(it) }

    @Provides
    public fun provideCoroutineScope(@MainCoroutineScope scope: CoroutineScope): CoroutineScope =
        scope
}
