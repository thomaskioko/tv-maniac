package com.thomaskioko.tvmaniac.seasondetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.data.seasondetails.Loading
import com.thomaskioko.tvmaniac.data.seasondetails.LoadingError
import com.thomaskioko.tvmaniac.data.seasondetails.SeasonDetailsLoaded
import com.thomaskioko.tvmaniac.data.seasondetails.SeasonDetailsState
import com.thomaskioko.tvmaniac.data.seasondetails.model.SeasonDetails
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.components.CollapsableContent
import com.thomaskioko.tvmaniac.seasondetails.components.WatchNextContent

@Composable
fun SeasonDetailsRoute(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SeasonDetailsViewModel = hiltViewModel(),
    initialSeasonName: String? = null,
    onEpisodeClicked: (Long) -> Unit = {},
) {

    val viewState by viewModel.state.collectAsStateWithLifecycle()

    SeasonDetailScreen(
        state = viewState,
        onBackClicked = onBackClicked,
        modifier = modifier,
        seasonName = initialSeasonName,
        onEpisodeClicked = onEpisodeClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeasonDetailScreen(
    state: SeasonDetailsState,
    onBackClicked: () -> Unit,
    seasonName: String?,
    modifier: Modifier = Modifier,
    onEpisodeClicked: (Long) -> Unit,
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopBar(
                title = (state as? SeasonDetailsLoaded)?.showTitle ?: "",
                navigateUp = onBackClicked
            )
        },
        modifier = modifier
            .statusBarsPadding(),
        content = { contentPadding ->
            when (state) {
                Loading -> LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )

                is LoadingError ->
                    ErrorUi(
                        errorMessage = state.message,
                        onRetry = {},
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )

                is SeasonDetailsLoaded -> {
                    SeasonContent(
                        seasonsEpList = state.seasonDetailsList,
                        initialSeasonName = seasonName,
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

            item { Spacer(modifier = Modifier.height(64.dp)) }

            item { WatchNextContent(seasonsEpList.firstOrNull()?.episodes) }

            item { Spacer(modifier = Modifier.height(16.dp)) }

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
private fun AllSeasonsTitle(
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.title_all_seasons),
            style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.secondary),
        )

        Spacer(modifier = Modifier.height(8.dp))
    }

}


@ThemePreviews
@Composable
private fun SeasonDetailScreenPreview(
    @PreviewParameter(SeasonPreviewParameterProvider::class)
    state: SeasonDetailsState
) {
    TvManiacTheme {
        Surface {
            SeasonDetailScreen(
                state = state,
                seasonName = "Loki",
                onBackClicked = {},
                onEpisodeClicked = {},
            )
        }
    }
}
