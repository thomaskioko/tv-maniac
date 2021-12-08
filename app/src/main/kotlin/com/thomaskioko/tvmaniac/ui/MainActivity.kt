package com.thomaskioko.tvmaniac.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.thomaskioko.tvmaniac.compose.theme.DarkColors
import com.thomaskioko.tvmaniac.compose.theme.LightColors
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.home.HomeScreen
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.settings.api.TvManiacPreferences
import com.thomaskioko.tvmaniac.settings.domain.shouldUseDarkColors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var composeNavigationFactories: @JvmSuppressWildcards Set<ComposeNavigationFactory>

    @Inject
    lateinit var themePreference: TvManiacPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProvideWindowInsets(consumeWindowInsets = false) {
                TvManiacTheme(darkTheme = themePreference.shouldUseDarkColors()) {
                    SetupTheme()
                    HomeScreen(composeNavigationFactories)
                }
            }
        }
    }

    @Composable
    private fun SetupTheme() {
        val systemUiController = rememberSystemUiController()
        val isLightTheme = !themePreference.shouldUseDarkColors()

        val systemBarColor = MaterialTheme.colors.surface.copy(alpha = 0.0f)
        val transparentColor: (Color) -> Color = { original ->
            systemBarColor.compositeOver(original)
        }
        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = isLightTheme
            )

            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = isLightTheme,
                transformColorForLightContent = transparentColor
            )

            systemUiController.setNavigationBarColor(
                color = if (isLightTheme) LightColors.surface else DarkColors.primary,
                darkIcons = isLightTheme,
                navigationBarContrastEnforced = false,
                transformColorForLightContent = transparentColor
            )
        }
    }
}
