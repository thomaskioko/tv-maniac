package com.thomaskioko.tvmaniac.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomNavigationItem
import com.thomaskioko.tvmaniac.compose.components.TvManiacNavigationBar

@Composable
fun MainUiContent(
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            val isBottomNavVisible = navigator.size <= 1
            AnimatedVisibility(
                visible = isBottomNavVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BottomNavigationContent(
                    navigator = navigator,
                    modifier = modifier,
                )
            }
        },
    ) { paddingValues ->
        FadeTransition(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = paddingValues
                        .calculateBottomPadding()
                        .plus(4.dp),
                ),
            navigator = navigator,
            animationSpec = tween(
                durationMillis = 0,
                delayMillis = 0,
                easing = LinearEasing,
            ),
        )
    }
}

@Composable
internal fun BottomNavigationContent(
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    TvManiacNavigationBar(
        modifier = modifier,
    ) {
        remember { BottomBarItems.entries }.fastForEach {
            val isSelected = it.isSelected(navigator)
            TvManiacBottomNavigationItem(
                imageVector = it.imageVector,
                title = stringResource(id = it.stringResourceId),
                selected = isSelected,
                onClick = {
                    if (isSelected) return@TvManiacBottomNavigationItem
                    navigator.replace(it.screen())
                },
            )
        }
    }
}
