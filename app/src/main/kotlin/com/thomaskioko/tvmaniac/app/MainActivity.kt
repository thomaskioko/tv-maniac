package com.thomaskioko.tvmaniac.app

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.thomaskioko.tvmaniac.RootScreen
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.inject.ActivityComponent

class MainActivity : ComponentActivity() {
    private lateinit var component: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        component = ActivityComponent.Companion.create(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        component.traktAuthManager.registerResult()

        enableEdgeToEdge()

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val scaleX = ObjectAnimator.ofFloat(splashScreenView.iconView, View.SCALE_X, 1f, 0f)
            val scaleY = ObjectAnimator.ofFloat(splashScreenView.iconView, View.SCALE_Y, 1f, 0f)
            val alpha = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)

            listOf(scaleX, scaleY, alpha).forEach { animator ->
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.duration = 300L
            }

            alpha.doOnEnd { splashScreenView.remove() }

            scaleX.start()
            scaleY.start()
            alpha.start()
        }

        setContent {
            val themeState by component.rootPresenter.themeState.collectAsState()
            val appTheme = themeState.appTheme
            val useDarkTheme = shouldUseDarkTheme(appTheme)

            splashScreen.setKeepOnScreenCondition { themeState.isFetching }

            DisposableEffect(useDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.Companion.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) {
                        useDarkTheme
                    },
                    navigationBarStyle = SystemBarStyle.Companion.auto(
                        lightScrim,
                        darkScrim,
                    ) {
                        useDarkTheme
                    },
                )
                onDispose {}
            }

            TvManiacTheme(appTheme = appTheme) { RootScreen(rootPresenter = component.rootPresenter) }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(appTheme: AppTheme): Boolean {
    return when (appTheme) {
        AppTheme.SYSTEM_THEME -> isSystemInDarkTheme()
        else -> appTheme.isDark
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
