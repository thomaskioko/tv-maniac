package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.arkivanov.essenty.statekeeper.consumeRequired
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetConfig
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

internal class DefaultSheetConfigSerializerTest {

    private val bindings: Set<SheetConfigBinding<*>> = setOf(
        SheetConfigBinding(EpisodeSheetConfig::class, EpisodeSheetConfig.serializer()),
    )

    private val serializer = DefaultSheetConfigSerializer(bindings).serializer

    @Test
    fun `should round trip episode sheet config`() {
        val config = EpisodeSheetConfig(episodeId = 42L, source = ScreenSource.DISCOVER)

        roundTrip(config) shouldBe config
    }

    @Test
    fun `should preserve field values given EpisodeSheetConfig round trip`() {
        val original = EpisodeSheetConfig(episodeId = 1234L, source = ScreenSource.CALENDAR)

        val restored = roundTrip(original).shouldBeInstanceOf<EpisodeSheetConfig>()

        restored.episodeId shouldBe 1234L
        restored.source shouldBe ScreenSource.CALENDAR
    }

    @Test
    fun `should register each provided binding exactly once`() {
        val serializer = DefaultSheetConfigSerializer(bindings).serializer

        serializer.descriptor.serialName shouldBe "PolymorphicSerializer"
    }

    private fun roundTrip(config: SheetConfig): SheetConfig {
        val container = SerializableContainer(
            value = config,
            strategy = serializer,
        )
        return container.consumeRequired(serializer)
    }
}
