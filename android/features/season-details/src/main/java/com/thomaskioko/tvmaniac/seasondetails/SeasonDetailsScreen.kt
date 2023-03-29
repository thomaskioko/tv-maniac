package com.thomaskioko.tvmaniac.seasondetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.CircularLoadingView
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.data.seasondetails.Loading
import com.thomaskioko.tvmaniac.data.seasondetails.LoadingError
import com.thomaskioko.tvmaniac.data.seasondetails.SeasonDetailsLoaded
import com.thomaskioko.tvmaniac.data.seasondetails.model.SeasonDetails
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.components.CollapsableContent
import com.thomaskioko.tvmaniac.seasondetails.components.WatchNextContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonDetailsScreen(
    viewModel: SeasonDetailsViewModel,
    navigateUp: () -> Unit,
    initialSeasonName: String? = null,
    onEpisodeClicked: (Long) -> Unit = {},
) {

    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()


    Scaffold(
        topBar = {
            TopBar(
                title = (viewState as? SeasonDetailsLoaded)?.showTitle ?: "",
                navigateUp = navigateUp
            )
        },
        modifier = Modifier
            .statusBarsPadding(),
        content = { contentPadding ->
            when (viewState) {
                Loading -> CircularLoadingView()
                is LoadingError -> ErrorUi(
                    errorMessage = (viewState as LoadingError).message,
                    onRetry = {},
                )

                is SeasonDetailsLoaded -> {
                    SeasonContent(
                        seasonsEpList = (viewState as SeasonDetailsLoaded).episodeList,
                        initialSeasonName = initialSeasonName,
                        onEpisodeClicked = onEpisodeClicked,
                        listState = listState,
                        contentPadding = contentPadding,
                    )
                }
            }
        }
    )
}


@Composable
private fun TopBar(
    title: String,
    navigateUp: () -> Unit
) {
    TvManiacTopBar(
        title = title,
        showNavigationIcon = true,
        onBackClick = navigateUp,
    )
}

@Composable
private fun SeasonContent(
    seasonsEpList: List<SeasonDetails>?,
    initialSeasonName: String?,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onEpisodeClicked: (Long) -> Unit = {},
) {
    seasonsEpList?.let {

        LaunchedEffect(initialSeasonName) {
            val initialIndex = seasonsEpList.indexOfFirst { it.seasonName == initialSeasonName }

            if (initialIndex in 0 until seasonsEpList.count()) {
                listState.animateScrollToItem(index = initialIndex)
            }
        }

        val collapsedState = remember(seasonsEpList) {
            seasonsEpList.map {
                it.seasonName != initialSeasonName
            }.toMutableStateList()
        }

        LazyColumn(
            state = listState,
            contentPadding = contentPadding.copy(copyTop = false),
        ) {

            item { ColumnSpacer(64) }

            item { WatchNextContent(seasonsEpList.firstOrNull()?.episodes) }

            item { ColumnSpacer(16) }

            item { AllSeasonsTitle() }

            itemsIndexed(seasonsEpList) { index, season ->
                CollapsableContent(
                    episodesCount = season.episodeCount,
                    headerTitle = season.seasonName,
                    watchProgress = season.watchProgress,
                    episodeList = season.episodes,
                    collapsed = collapsedState[index],
                    onEpisodeClicked = { onEpisodeClicked(it) },
                    onSeasonHeaderClicked = { collapsedState[index] = !collapsedState[index] }
                )
            }
        }
    }
}


@Composable
private fun AllSeasonsTitle() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        ColumnSpacer(8)

        Text(
            text = stringResource(id = R.string.title_all_seasons),
            style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.secondary),
        )
    }

    ColumnSpacer(8)
}


@ThemePreviews
@Composable
fun SeasonsContentPreview() {
    TvManiacTheme {
        Surface {
            SeasonContent(
                seasonsEpList = seasonsEpList,
                initialSeasonName = "Specials",
                onEpisodeClicked = {},
                contentPadding = PaddingValues(),
                listState = LazyListState(),
            )
        }
    }
}
