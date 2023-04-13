package com.thomaskioko.tvmaniac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.thomaskioko.tvmaniac.compose.components.ConnectionStatus
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.core.networkutil.ConnectionState
import com.thomaskioko.tvmaniac.inject.MainActivityComponent
import com.thomaskioko.tvmaniac.inject.create
import com.thomaskioko.tvmaniac.settings.shouldUseDarkColors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var component: MainActivityComponent

    private val viewModel: MainActivityViewModel by viewModels {
        viewModelFactory {
            addInitializer(MainActivityViewModel::class) { component.viewModel() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = MainActivityComponent::class.create(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        component.traktAuthManager.registerResult()

        setContent {
            val state = viewModel.state.collectAsState()

            val systemUiController = rememberSystemUiController()
            val darkTheme = state.value.theme.shouldUseDarkColors()

            DisposableEffect(systemUiController, darkTheme) {
                systemUiController.systemBarsDarkContentEnabled = !darkTheme
                onDispose {}
            }

            TvManiacTheme(darkTheme = darkTheme) {
                Surface {
                }
            }

            ConnectivityStatus(state.value.connectionState)
        }
    }


    @ExperimentalAnimationApi
    @ExperimentalCoroutinesApi
    @Composable
    fun ConnectivityStatus(connectionState: ConnectionState) {
        val isConnected = connectionState === ConnectionState.ConnectionAvailable

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
