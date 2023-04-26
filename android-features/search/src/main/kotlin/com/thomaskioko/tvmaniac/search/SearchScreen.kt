package com.thomaskioko.tvmaniac.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
    ) {
        SearchBar(
            hint = stringResource(id = R.string.msg_search_show_hint),
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = {},
        )
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
