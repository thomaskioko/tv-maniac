package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.flags.ContinueWatchingNitroFlag
import com.thomaskioko.tvmaniac.featureflags.flags.SimklLoginFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test

class FlagMetroBindingTest {

    @Test
    fun `should resolve concrete flag class from graph`() {
        val graph = createGraphFactory<FlagTestGraph.Factory>().create(DefaultTestRemoteConfig)

        graph.nitroFlag.shouldBeInstanceOf<ContinueWatchingNitroFlag>()
        graph.simklFlag.shouldBeInstanceOf<SimklLoginFlag>()
    }

    @Test
    fun `should contribute every flag class to FeatureFlag multibinding`() {
        val graph = createGraphFactory<FlagTestGraph.Factory>().create(DefaultTestRemoteConfig)

        graph.flags shouldHaveSize 2
        graph.flags shouldContain graph.nitroFlag
        graph.flags shouldContain graph.simklFlag
    }

    @Test
    fun `should reuse same instance for concrete accessor and set entry`() {
        val graph = createGraphFactory<FlagTestGraph.Factory>().create(DefaultTestRemoteConfig)
        val concrete = graph.nitroFlag

        val fromSet = graph.flags.first { it is ContinueWatchingNitroFlag }
        fromSet shouldBe concrete
    }
}

@DependencyGraph(AppScope::class)
internal interface FlagTestGraph {
    val nitroFlag: ContinueWatchingNitroFlag
    val simklFlag: SimklLoginFlag
    val flags: Set<FeatureFlag>

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides remote: FeatureFlagsRemoteConfig): FlagTestGraph
    }
}

private object DefaultTestRemoteConfig : FeatureFlagsRemoteConfig {
    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> = flowOf(default)
    override fun observeSource(key: String): Flow<FeatureFlagSource> = flowOf(FeatureFlagSource.Firebase)
    override suspend fun refresh() = Unit
    override suspend fun setDefaults(defaults: Map<String, Boolean>) = Unit
}
