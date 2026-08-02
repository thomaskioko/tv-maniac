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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.outlined.ViewHeadline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PremiumOverlay
import com.thomaskioko.tvmaniac.compose.components.SearchBar
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.continuewatching.ui.ContinueWatchingScreen
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_filter
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_search
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_toggle_list_style
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_layout_compact
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_layout_detailed
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_layout_grid
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_layout_list
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_layouts_locked_message
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_layouts_locked_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_premium_badge
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_upgrade_to_premium
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_my_shows
import com.thomaskioko.tvmaniac.i18n.MR.strings.msg_search_show_hint
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsAction
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsPresenter
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsState
import com.thomaskioko.tvmaniac.startwatching.ui.StartWatchingScreen
import com.thomaskioko.tvmaniac.testtags.myshows.MyShowsTestTags
import io.github.thomaskioko.codegen.annotations.TabUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

private val PremiumLockCardWidth = 280.dp
private val PremiumLockCardMinHeight = 240.dp

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
    var layoutMenuExpanded by remember { mutableStateOf(false) }
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
            layoutMenuExpanded = layoutMenuExpanded,
            onLayoutMenuExpandedChange = { layoutMenuExpanded = it },
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
    layoutMenuExpanded: Boolean,
    onLayoutMenuExpandedChange: (Boolean) -> Unit,
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
                    SearchBar(
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
                        state = state,
                        showLayoutMenu = state.selectedPage == 0,
                        onAction = onAction,
                        onSearchClick = { onAction(MyShowsAction.ToggleSearch) },
                        onSortClick = onSortClick,
                        layoutMenuExpanded = layoutMenuExpanded,
                        onLayoutMenuExpandedChange = onLayoutMenuExpandedChange,
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
    state: MyShowsState,
    showLayoutMenu: Boolean,
    onAction: (MyShowsAction) -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    layoutMenuExpanded: Boolean,
    onLayoutMenuExpandedChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showLayoutMenu) {
            LayoutMenu(
                currentStyle = state.listStyle,
                isLocked = state.isListStyleLocked,
                expanded = layoutMenuExpanded,
                onExpandedChange = onLayoutMenuExpandedChange,
                onAction = onAction,
            )
        } else {
            Spacer(Modifier.size(TvManiacSpacing.xxLarge))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        ) {
            Text(
                text = menu_item_my_shows.resolve(context),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (state.showRefreshIndicator) {
                LoadingIndicator(
                    modifier = Modifier
                        .testTag(MyShowsTestTags.MY_SHOWS_INDICATOR)
                        .size(20.dp),
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
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

@Composable
internal fun LayoutMenu(
    currentStyle: ListStyle,
    isLocked: Boolean,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onAction: (MyShowsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Box(modifier = modifier) {
        IconButton(
            onClick = { onExpandedChange(true) },
            modifier = Modifier.testTag(MyShowsTestTags.LAYOUT_MENU_BUTTON_TEST_TAG),
        ) {
            Icon(
                imageVector = currentStyle.icon(),
                contentDescription = cd_toggle_list_style.resolve(context),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.testTag(MyShowsTestTags.LAYOUT_MENU_TEST_TAG),
        ) {
            LayoutMenuItem(
                label = label_layout_grid.resolve(context),
                style = ListStyle.GRID,
                currentStyle = currentStyle,
                tag = MyShowsTestTags.LAYOUT_MENU_ITEM_GRID_TEST_TAG,
                onClick = {
                    onAction(MyShowsAction.ChangeListStyle(ListStyle.GRID))
                    onExpandedChange(false)
                },
            )
            LayoutMenuItem(
                label = label_layout_list.resolve(context),
                style = ListStyle.LIST,
                currentStyle = currentStyle,
                tag = MyShowsTestTags.LAYOUT_MENU_ITEM_LIST_TEST_TAG,
                onClick = {
                    onAction(MyShowsAction.ChangeListStyle(ListStyle.LIST))
                    onExpandedChange(false)
                },
            )

            PremiumOverlay(
                locked = isLocked,
                badgeText = label_premium_badge.resolve(context),
                title = label_layouts_locked_title.resolve(context),
                message = label_layouts_locked_message.resolve(context),
                actionText = label_upgrade_to_premium.resolve(context),
                onActionClick = {
                    onAction(MyShowsAction.UpgradeClicked)
                    onExpandedChange(false)
                },
                modifier = Modifier
                    .width(PremiumLockCardWidth)
                    .testTag(MyShowsTestTags.LAYOUT_MENU_LOCKED_SECTION_TEST_TAG)
                    .then(if (isLocked) Modifier.heightIn(min = PremiumLockCardMinHeight) else Modifier),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LayoutMenuItem(
                        label = label_layout_compact.resolve(context),
                        style = ListStyle.COMPACT,
                        currentStyle = currentStyle,
                        tag = MyShowsTestTags.LAYOUT_MENU_ITEM_COMPACT_TEST_TAG,
                        onClick = {
                            onAction(MyShowsAction.ChangeListStyle(ListStyle.COMPACT))
                            onExpandedChange(false)
                        },
                    )
                    LayoutMenuItem(
                        label = label_layout_detailed.resolve(context),
                        style = ListStyle.DETAILED,
                        currentStyle = currentStyle,
                        tag = MyShowsTestTags.LAYOUT_MENU_ITEM_DETAILED_TEST_TAG,
                        onClick = {
                            onAction(MyShowsAction.ChangeListStyle(ListStyle.DETAILED))
                            onExpandedChange(false)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LayoutMenuItem(
    label: String,
    style: ListStyle,
    currentStyle: ListStyle,
    tag: String,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = label) },
        leadingIcon = { Icon(imageVector = style.icon(), contentDescription = null) },
        trailingIcon = if (style == currentStyle) {
            { Icon(imageVector = Icons.Filled.Check, contentDescription = null) }
        } else {
            null
        },
        onClick = onClick,
        modifier = Modifier.testTag(tag),
    )
}

private fun ListStyle.icon(): ImageVector = when (this) {
    ListStyle.GRID -> Icons.Outlined.GridView
    ListStyle.LIST -> Icons.AutoMirrored.Outlined.List
    ListStyle.COMPACT -> Icons.Outlined.ViewHeadline
    ListStyle.DETAILED -> Icons.Outlined.ViewAgenda
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
