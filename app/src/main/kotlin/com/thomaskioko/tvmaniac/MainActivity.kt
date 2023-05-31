package com.thomaskioko.tvmaniac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.home.HomeScreen
import com.thomaskioko.tvmaniac.inject.MainActivityComponent
import com.thomaskioko.tvmaniac.inject.create
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.settings.shouldUseDarkColors
import com.thomaskioko.tvmaniac.util.extensions.unsafeLazy

class MainActivity : ComponentActivity() {
    private lateinit var component: MainActivityComponent

    private val navFactorySet: Set<ComposeNavigationFactory> by unsafeLazy { component.navFactorySet }

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
                    HomeScreen(navFactorySet)
                }
            }
        }
    }
}
