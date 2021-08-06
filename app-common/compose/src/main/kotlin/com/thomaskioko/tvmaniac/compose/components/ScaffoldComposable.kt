package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun TvManiacScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    appBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = appBar,
        bottomBar = bottomBar,
        content = content
    )
}
