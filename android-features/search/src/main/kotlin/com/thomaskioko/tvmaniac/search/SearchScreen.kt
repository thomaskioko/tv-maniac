package com.thomaskioko.tvmaniac.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.navigation.extensions.viewModel
import com.thomaskioko.tvmaniac.resources.R
import me.tatarka.inject.annotations.Inject

typealias Search = @Composable () -> Unit

@Inject
@Composable
fun Search(
    viewModelFactory: () -> SearchViewModel,
) {
    SearchScreen(
        viewModel = viewModel(factory = viewModelFactory),
    )
}

@Composable
internal fun SearchScreen(
    viewModel: SearchViewModel,
    modifier: Modifier = Modifier,
) {
    SearchScreen(
        modifier = modifier,
    )
}

@Composable
internal fun SearchScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier
            .statusBarsPadding(),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                ),
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                hint = stringResource(id = R.string.msg_search_show_hint),
                onValueChange = {},
            )
        }
    }
}

@ThemePreviews
@Composable
fun SearchContentPreview() {
    TvManiacTheme {
        Surface {
            SearchScreen()
        }
    }
}
