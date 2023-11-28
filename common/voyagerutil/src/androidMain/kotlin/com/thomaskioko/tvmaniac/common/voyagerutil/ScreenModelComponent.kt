package com.thomaskioko.tvmaniac.common.voyagerutil

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.thomaskioko.tvmaniac.common.voyagerutil.inject.LocalScreenModels

actual interface ScreenModelComponent : PlatformScreenModelComponent

@Composable
inline fun <reified VM : ScreenModel> Screen.viewModel(
    tag: String? = null,
    crossinline factory: @DisallowComposableCalls ScreenModelComponent.() -> VM,
): VM {
    val viewModelFactory = LocalScreenModels.current
    return rememberScreenModel(tag) { viewModelFactory.factory() }
}
