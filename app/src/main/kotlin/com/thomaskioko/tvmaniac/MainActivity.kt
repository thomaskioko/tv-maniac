package com.thomaskioko.tvmaniac

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.datastore.api.Theme
import com.thomaskioko.tvmaniac.inject.MainActivityComponent
import com.thomaskioko.tvmaniac.inject.create
import com.thomaskioko.tvmaniac.navigation.Loading
import com.thomaskioko.tvmaniac.navigation.ThemeLoaded
import com.thomaskioko.tvmaniac.navigation.ThemeState

class MainActivity : ComponentActivity() {
    private lateinit var component: MainActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        component = MainActivityComponent.create(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        component.traktAuthManager.registerResult()

        enableEdgeToEdge()

        setContent {
            val themeState by component.presenter.state.collectAsState()
            val darkTheme = shouldUseDarkTheme(themeState)

            splashScreen.setKeepOnScreenCondition {
                when (themeState) {
                    Loading -> true
                    is ThemeLoaded -> false
                }
            }

            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }

            TvManiacTheme(darkTheme = darkTheme) {
                component.rootScreen()
            }
        }
    }
}

/**
 * Returns `true` if dark theme should be used, as a function of the [uiState] and the
 * current system context.
 */
@Composable
private fun shouldUseDarkTheme(
    uiState: ThemeState,
): Boolean = when (uiState) {
    Loading -> isSystemInDarkTheme()
    is ThemeLoaded -> when (uiState.theme) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        Theme.SYSTEM -> isSystemInDarkTheme()
    }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
