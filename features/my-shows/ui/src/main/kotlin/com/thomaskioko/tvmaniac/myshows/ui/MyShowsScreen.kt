package com.thomaskioko.tvmaniac.myshows.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.continuewatching.ui.ContinueWatchingScreen
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_filter
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_search
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_toggle_list_style
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_my_shows
import com.thomaskioko.tvmaniac.i18n.MR.strings.msg_search_show_hint
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsAction
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsPresenter
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsState
import com.thomaskioko.tvmaniac.myshows.ui.component.Searchbar
import com.thomaskioko.tvmaniac.startwatching.ui.StartWatchingScreen
import com.thomaskioko.tvmaniac.testtags.myshows.MyShowsTestTags
import io.github.thomaskioko.codegen.annotations.TabUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@TabUi(presenter = MyShowsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun MyShowsScreen(
    presenter: MyShowsPresenter,
    modifier: Modifier = Modifier,
) {
    val myShowsState by presenter.state.collectAsState()
    val continueWatchingState by presenter.continueWatchingPresenter.state.collectAsState()
    val startWatchingState by presenter.startWatchingPresenter.state.collectAsState()

    MyShowsScreen(
        state = myShowsState,
        tabs = persistentListOf(myShowsState.continueWatchingTitle, myShowsState.startWatchingTitle),
        modifier = modifier,
        onAction = presenter::dispatch,
        continueWatchingContent = {
            ContinueWatchingScreen(
                state = continueWatchingState,
                onAction = presenter.continueWatchingPresenter::dispatch,
                modifier = Modifier.fillMaxSize(),
            )
        },
        startWatchingContent = {
            StartWatchingScreen(
                state = startWatchingState,
                onAction = presenter.startWatchingPresenter::dispatch,
                modifier = Modifier.fillMaxSize(),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyShowsScreen(
    state: MyShowsState,
    tabs: ImmutableList<String>,
    modifier: Modifier = Modifier,
    onAction: (MyShowsAction) -> Unit = {},
    continueWatchingContent: @Composable () -> Unit = {},
    startWatchingContent: @Composable () -> Unit = {},
) {
    val pagerState = rememberPagerState(
        initialPage = state.selectedPage,
        pageCount = { tabs.size },
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showSortOptions by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.selectedPage) {
        if (pagerState.currentPage != state.selectedPage) {
            pagerState.animateScrollToPage(state.selectedPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { onAction(MyShowsAction.SelectPage(it)) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Toolbar(
            state = state,
            onAction = onAction,
            scrollBehavior = scrollBehavior,
            onSortClick = { showSortOptions = true },
        )

        SecondaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag(MyShowsTestTags.TAB_ROW),
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(pagerState.currentPage),
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier.testTag(
                        if (index == 0) MyShowsTestTags.CONTINUE_WATCHING_TAB else MyShowsTestTags.START_WATCHING_TAB,
                    ),
                    selected = pagerState.currentPage == index,
                    onClick = { onAction(MyShowsAction.SelectPage(index)) },
                    text = {
                        Text(text = title, style = MaterialTheme.typography.titleSmall)
                    },
                    selectedContentColor = MaterialTheme.colorScheme.secondary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .testTag(MyShowsTestTags.HORIZONTAL_PAGER)
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            when (page) {
                0 -> continueWatchingContent()
                1 -> startWatchingContent()
            }
        }
    }

    if (showSortOptions) {
        ModalBottomSheet(
            onDismissRequest = { showSortOptions = false },
            sheetState = sheetState,
            modifier = Modifier.testTag(MyShowsTestTags.SORT_SHEET_TEST_TAG),
        ) {
            MyShowsSortOptionsContent(
                selectedSortOption = state.sortOption,
                onSortOptionSelected = { sortOption ->
                    onAction(MyShowsAction.ChangeSortOption(sortOption))
                    scope.launch {
                        sheetState.hide()
                        showSortOptions = false
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    state: MyShowsState,
    onAction: (MyShowsAction) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    onSortClick: () -> Unit,
) {
    val context = LocalContext.current

    BackHandler(enabled = state.isSearchActive) {
        onAction(MyShowsAction.ClearQuery)
        onAction(MyShowsAction.ToggleSearch)
    }

    TvManiacTopBar(
        title = {
            AnimatedContent(
                targetState = state.isSearchActive,
                transitionSpec = {
                    (scaleIn(animationSpec = spring()) + fadeIn()) togetherWith
                        (scaleOut(animationSpec = spring()) + fadeOut())
                },
                label = "search_expansion_animation",
            ) { expanded ->
                if (expanded) {
                    Searchbar(
                        modifier = Modifier.testTag(MyShowsTestTags.SEARCH_BAR_TEST_TAG),
                        query = state.query,
                        hint = msg_search_show_hint.resolve(context),
                        onQueryChanged = { onAction(MyShowsAction.QueryChanged(it)) },
                        onCloseClick = {
                            onAction(MyShowsAction.ClearQuery)
                            onAction(MyShowsAction.ToggleSearch)
                        },
                    )
                } else {
                    CollapsedToolbarContent(
                        isGridMode = state.isGridMode,
                        showListStyleToggle = state.selectedPage == 0,
                        onToggleListStyle = { onAction(MyShowsAction.ChangeListStyle(state.isGridMode)) },
                        onSearchClick = { onAction(MyShowsAction.ToggleSearch) },
                        onSortClick = onSortClick,
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
private fun CollapsedToolbarContent(
    isGridMode: Boolean,
    showListStyleToggle: Boolean,
    onToggleListStyle: () -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showListStyleToggle) {
            IconButton(
                modifier = Modifier.testTag(MyShowsTestTags.TOGGLE_LIST_STYLE_BUTTON_TEST_TAG),
                onClick = onToggleListStyle,
            ) {
                Icon(
                    imageVector = if (isGridMode) Icons.AutoMirrored.Outlined.List else Icons.Outlined.GridView,
                    contentDescription = cd_toggle_list_style.resolve(context),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }

        Text(
            text = menu_item_my_shows.resolve(context),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(
                modifier = Modifier.testTag(MyShowsTestTags.SEARCH_BUTTON_TEST_TAG),
                onClick = onSearchClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = cd_search.resolve(context),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            IconButton(
                modifier = Modifier.testTag(MyShowsTestTags.SORT_BUTTON_TEST_TAG),
                onClick = onSortClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = cd_filter.resolve(context),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
internal fun MyShowsScreenPreview() {
    MyShowsScreen(
        state = MyShowsState(
            continueWatchingTitle = "Continue Watching",
            startWatchingTitle = "Start Watching",
        ),
        tabs = persistentListOf("Continue Watching", "Start Watching"),
        continueWatchingContent = { },
        startWatchingContent = { },
    )
}
