package com.thomaskioko.tvmaniac.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.thomaskioko.tvmaniac.compose.components.ConnectionStatus
import com.thomaskioko.tvmaniac.compose.theme.DarkColors
import com.thomaskioko.tvmaniac.compose.theme.LightColors
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.core.util.network.ConnectionState
import com.thomaskioko.tvmaniac.core.util.network.ObserveConnectionState
import com.thomaskioko.tvmaniac.home.HomeScreen
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.settings.api.SettingsRepository
import com.thomaskioko.tvmaniac.settings.shouldUseDarkColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var composeNavigationFactories: @JvmSuppressWildcards Set<ComposeNavigationFactory>

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var observeNetwork: ObserveConnectionState

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TvManiacTheme(darkTheme = settingsRepository.shouldUseDarkColors()) {
                SetupTheme()
                HomeScreen(composeNavigationFactories)
            }

            ConnectivityStatus(observeNetwork)
        }
    }

    @Composable
    private fun SetupTheme() {
        val systemUiController = rememberSystemUiController()
        val isLightTheme = !settingsRepository.shouldUseDarkColors()

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

    @ExperimentalAnimationApi
    @ExperimentalCoroutinesApi
    @Composable
    fun ConnectivityStatus(observeNetwork: ObserveConnectionState) {
        val connection by connectivityState(observeNetwork)
        val isConnected = connection === ConnectionState.ConnectionAvailable

        var visibility by remember { mutableStateOf(false) }

        LaunchedEffect(isConnected) {
            visibility = if (!isConnected) {
                true
            } else {
                delay(2000)
                false
            }
        }

        AnimatedVisibility(
            visible = visibility,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            ConnectionStatus(isConnected = isConnected)
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun connectivityState(observeNetwork: ObserveConnectionState): State<ConnectionState> {
    return produceState(initialValue = observeNetwork.currentConnectivityState) {
        observeNetwork.observeConnectivityAsFlow()
            .distinctUntilChanged()
            .collect { value = it }
    }
}
