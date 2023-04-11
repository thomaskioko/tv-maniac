package com.thomaskioko.tvmaniac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.thomaskioko.tvmaniac.base.extensions.unsafeLazy
import com.thomaskioko.tvmaniac.compose.components.ConnectionStatus
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.core.networkutil.ConnectionState
import com.thomaskioko.tvmaniac.core.networkutil.NetworkRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.inject.MainActivityComponent
import com.thomaskioko.tvmaniac.inject.create
import com.thomaskioko.tvmaniac.settings.shouldUseDarkColors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var component: MainActivityComponent

    private val datastoreRepository: DatastoreRepository by unsafeLazy { component.datastoreRepository }
    private val networkRepository: NetworkRepository by unsafeLazy { component.networkRepository }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = MainActivityComponent::class.create(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            val darkTheme = datastoreRepository.shouldUseDarkColors()

            DisposableEffect(systemUiController, darkTheme) {
                systemUiController.systemBarsDarkContentEnabled = !darkTheme
                onDispose {}
            }

            TvManiacTheme(darkTheme = darkTheme) {
                Surface {
                }
            }

            ConnectivityStatus(networkRepository)
        }
    }


    @ExperimentalAnimationApi
    @ExperimentalCoroutinesApi
    @Composable
    fun ConnectivityStatus(observeNetwork: NetworkRepository) {
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
fun connectivityState(observeNetwork: NetworkRepository): State<ConnectionState> {
    return produceState(initialValue = observeNetwork.connectivityState) {
        observeNetwork.observeConnectionState()
            .distinctUntilChanged()
            .collect { value = it }
    }
}
