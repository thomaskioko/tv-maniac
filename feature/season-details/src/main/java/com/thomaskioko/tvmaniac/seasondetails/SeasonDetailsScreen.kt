package com.thomaskioko.tvmaniac.seasondetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.thomaskioko.tvmaniac.common.voyagerutil.viewModel
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.seasondetails.Loading
import com.thomaskioko.tvmaniac.presentation.seasondetails.LoadingError
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsAction
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsLoaded
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsState
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Episode
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetails
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.components.CollapsableContent
import com.thomaskioko.tvmaniac.seasondetails.components.EpisodeItem
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList

data class SeasonDetailScreen(val id: Long) : Screen {
    @Composable
    override fun Content() {
        val screenModel = viewModel { seasonDetailsScreenModel(id) }
        val state by screenModel.state.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow

        SeasonDetailScreen(
            state = state,
            seasonName = null,
            onBackClicked = navigator::pop,
            onAction = screenModel::dispatch,
            onEpisodeClicked = {
                /** Uncomment this once the episode detail screen is implemented
                 navigator.push(ScreenRegistry.get(EpisodeDetailScreen(it)))
                 **/
            },
        )
    }
}

@Composable
internal fun SeasonDetailScreen(
    state: SeasonDetailsState,
    onBackClicked: () -> Unit,
    seasonName: String?,
    onAction: (SeasonDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
    onEpisodeClicked: (Long) -> Unit,
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopBar(
                title = (state as? SeasonDetailsLoaded)?.showTitle ?: "",
                navigateUp = onBackClicked,
            )
        },
        modifier = modifier.statusBarsPadding(),
        content = { contentPadding ->
            when (state) {
                Loading -> LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                )

                is LoadingError -> ErrorUi(
                    errorMessage = state.message,
                    onRetry = {},
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                )

                is SeasonDetailsLoaded -> {
                    SeasonContent(
                        seasonsEpList = state.seasonDetailsList,
                        initialSeasonName = seasonName,
                        onEpisodeClicked = onEpisodeClicked,
                        listState = listState,
                        contentPadding = contentPadding,
                        onAction = onAction,
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    title: String,
    navigateUp: () -> Unit,
) {
    TvManiacTopBar(
        title = title,
        showNavigationIcon = true,
        onBackClick = navigateUp,
        elevation = 8.dp,
    )
}

@Composable
private fun SeasonContent(
    seasonsEpList: ImmutableList<SeasonDetails>?,
    initialSeasonName: String?,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onEpisodeClicked: (Long) -> Unit = {},
    onAction: (SeasonDetailsAction) -> Unit,
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
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
        ) {
            item { Spacer(modifier = Modifier.height(64.dp)) }

            item {
                WatchNextContent(
                    episodeList = seasonsEpList.firstOrNull()?.episodes,
                    onAction = onAction,
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                LabelTitle(
                    label = stringResource(id = R.string.title_all_episodes),
                )
            }

            itemsIndexed(seasonsEpList) { index, season ->
                CollapsableContent(
                    episodesCount = season.episodeCount,
                    headerTitle = season.seasonName,
                    watchProgress = season.watchProgress,
                    episodeList = season.episodes,
                    collapsed = collapsedState[index],
                    onEpisodeClicked = { onEpisodeClicked(it) },
                    onSeasonHeaderClicked = { collapsedState[index] = !collapsedState[index] },
                    onAction = onAction,
                )
            }
        }
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun WatchNextContent(
    episodeList: ImmutableList<Episode>?,
    onAction: (SeasonDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
    onEpisodeClicked: () -> Unit = {},
) {
    episodeList?.let {
        LabelTitle(
            modifier = modifier
                .padding(top = 16.dp, bottom = 8.dp),
            label = stringResource(id = R.string.title_watch_next),
        )

        val lazyListState = rememberLazyListState()

        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
        ) {
            itemsIndexed(episodeList) { index, episode ->
                val value = if (index == 0) 0 else 8
                Spacer(modifier = Modifier.width(value.dp))

                EpisodeItem(
                    modifier = modifier.size(width = 320.dp, height = 90.dp),
                    imageUrl = episode.imageUrl,
                    title = episode.seasonEpisodeNumber,
                    episodeOverview = episode.overview,
                    onEpisodeClicked = onEpisodeClicked,
                    onAction = onAction,
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun LabelTitle(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.secondary),
        )
    }
}

@ThemePreviews
@Composable
private fun SeasonDetailScreenPreview(
    @PreviewParameter(SeasonPreviewParameterProvider::class) state: SeasonDetailsState,
) {
    TvManiacTheme {
        Surface {
            SeasonDetailScreen(
                state = state,
                seasonName = "Specials",
                onBackClicked = {},
                onEpisodeClicked = {},
                onAction = {},
            )
        }
    }
}
