package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.ui.graphics.Color
import com.thomaskioko.tvmaniac.domain.theme.Theme
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Test

internal class TvManiacColorSchemeTest {

    private val allThemes = listOf(
        LightTvManiacColorScheme,
        DarkTvManiacColorScheme,
        TerminalTvManiacColorScheme,
        AutumnTvManiacColorScheme,
        AquaTvManiacColorScheme,
        AmberTvManiacColorScheme,
        SnowTvManiacColorScheme,
        CrimsonTvManiacColorScheme,
    )

    @Test
    fun `should wrap the matching material color scheme for every theme`() {
        LightTvManiacColorScheme.material shouldBe LightColorScheme
        DarkTvManiacColorScheme.material shouldBe DarkColorScheme
        TerminalTvManiacColorScheme.material shouldBe TerminalColorScheme
        AutumnTvManiacColorScheme.material shouldBe AutumnColorScheme
        AquaTvManiacColorScheme.material shouldBe AquaColorScheme
        AmberTvManiacColorScheme.material shouldBe AmberColorScheme
        SnowTvManiacColorScheme.material shouldBe SnowColorScheme
        CrimsonTvManiacColorScheme.material shouldBe CrimsonColorScheme
    }

    @Test
    fun `should seed buttonBackground and onButtonBackground from the material secondary roles`() {
        allThemes.forEach { scheme ->
            scheme.buttonBackground shouldBe scheme.material.secondary
            scheme.onButtonBackground shouldBe scheme.material.onSecondary
        }
    }

    @Test
    fun `should share the same success, onSuccess, syncing, grey, scrim and onScrim across every theme`() {
        allThemes.forEach { scheme ->
            scheme.success shouldBe green
            scheme.onSuccess shouldBe Color.White
            scheme.syncing shouldBe syncing
            scheme.grey shouldBe grey
            scheme.scrim shouldBe Color.Black
            scheme.onScrim shouldBe Color.White
        }
    }

    @Test
    fun `should set surfaceVariant and onSurfaceVariant on light, dark and autumn`() {
        listOf(LightColorScheme, DarkColorScheme, AutumnColorScheme).forEach { scheme ->
            scheme.surfaceVariant shouldNotBe Color.Unspecified
            scheme.onSurfaceVariant shouldNotBe Color.Unspecified
        }
    }

    @Test
    fun `should map every Theme entry to its TvManiacColorScheme including system theme branching`() {
        Theme.LIGHT_THEME.toTvManiacColorScheme(isSystemInDarkTheme = false) shouldBe LightTvManiacColorScheme
        Theme.DARK_THEME.toTvManiacColorScheme(isSystemInDarkTheme = false) shouldBe DarkTvManiacColorScheme
        Theme.TERMINAL_THEME.toTvManiacColorScheme(isSystemInDarkTheme = false) shouldBe TerminalTvManiacColorScheme
        Theme.AUTUMN_THEME.toTvManiacColorScheme(isSystemInDarkTheme = false) shouldBe AutumnTvManiacColorScheme
        Theme.AQUA_THEME.toTvManiacColorScheme(isSystemInDarkTheme = false) shouldBe AquaTvManiacColorScheme
        Theme.AMBER_THEME.toTvManiacColorScheme(isSystemInDarkTheme = false) shouldBe AmberTvManiacColorScheme
        Theme.SNOW_THEME.toTvManiacColorScheme(isSystemInDarkTheme = false) shouldBe SnowTvManiacColorScheme
        Theme.CRIMSON_THEME.toTvManiacColorScheme(isSystemInDarkTheme = false) shouldBe CrimsonTvManiacColorScheme
        Theme.SYSTEM_THEME.toTvManiacColorScheme(isSystemInDarkTheme = true) shouldBe DarkTvManiacColorScheme
        Theme.SYSTEM_THEME.toTvManiacColorScheme(isSystemInDarkTheme = false) shouldBe LightTvManiacColorScheme
    }
}
