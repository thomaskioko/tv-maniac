package com.thomaskioko.tvmaniac.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.compose.components.GradientText
import com.thomaskioko.tvmaniac.compose.matchParent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    openDiscoverScreen: () -> Unit,
) {

    val welcomeText by remember { mutableStateOf("Tv Maniac") }
    Column(
        modifier = Modifier
            .matchParent()
            .background(MaterialTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GradientText(text = welcomeText)
    }

    LaunchedEffect(Unit) {

        viewModel.viewModelScope.launch {
            delay(1000)
            openDiscoverScreen()
        }
    }

}