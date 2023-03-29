package com.thomaskioko.tvmaniac.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavHostController,
) {
    SearchContent()
}

@Composable
private fun SearchContent() {
    val textState = remember { mutableStateOf(TextFieldValue()) }

    Column(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        SearchBar(
            hint = stringResource(id = R.string.msg_search_show_hint),
            textState = textState,
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = {}
        )
    }
}

@ThemePreviews
@Composable
fun SearchContentPreview() {
    TvManiacTheme {
        Surface {
            SearchContent()
        }
    }
}
