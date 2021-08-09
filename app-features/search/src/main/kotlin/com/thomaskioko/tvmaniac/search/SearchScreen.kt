package com.thomaskioko.tvmaniac.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.statusBarsPadding
import com.thomaskioko.tvmaniac.compose.R
import com.thomaskioko.tvmaniac.compose.components.SearchBar

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavHostController,
) {
    val textState = remember { mutableStateOf(TextFieldValue()) }

    Column(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Card(
            elevation = 8.dp,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            SearchBar(
                hint = stringResource(id = R.string.msg_search_show_hint),
                textState = textState
            ) {
                //TODO:: Add implementation
            }
        }
    }
}