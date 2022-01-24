package com.thomaskioko.tvmaniac.seasons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.util.iconButtonBackgroundScrim

@Composable
fun SeasonsScreen(
    viewModel: SeasonsViewModel,
    navigateUp: () -> Unit,
) {

    Scaffold(
        topBar = {
            TvManiacTopBar(
                title = {
                    Text(
                        text = "",
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = navigateUp,
                        modifier = Modifier.iconButtonBackgroundScrim()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background
            )
        },
        modifier = Modifier
            .statusBarsPadding(),
        content = { innerPadding ->
        }
    )
}
