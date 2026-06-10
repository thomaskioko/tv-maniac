package com.thomaskioko.tvmaniac.showlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.presentation.showlist.ShowListAction
import com.thomaskioko.tvmaniac.presentation.showlist.ShowListPresenter
import com.thomaskioko.tvmaniac.presentation.showlist.ShowListState
import com.thomaskioko.tvmaniac.testtags.showlist.ShowListTestTags
import io.github.thomaskioko.codegen.annotations.SheetUi

@SheetUi(presenter = ShowListPresenter::class, parentScope = ActivityScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun ShowList(
    presenter: ShowListPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { presenter.dispatch(ShowListAction.Dismiss) },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = {
            ShowListSheetTopBar(
                state = state,
                onAction = presenter::dispatch,
            )
        },
        modifier = modifier,
    ) {
        ShowListSheetContent(
            state = state,
            onAction = presenter::dispatch,
        )
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
internal fun ShowListSheetTopBar(
    state: ShowListState,
    onAction: (ShowListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = { onAction(ShowListAction.Dismiss) },
            modifier = Modifier.testTag(ShowListTestTags.CLOSE_BUTTON_TEST_TAG),
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = state.labels.sheetTitle,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        if (state.isLoggedIn && !state.showCreateListField) {
            IconButton(
                onClick = { onAction(ShowListAction.ShowCreateListField) },
                modifier = Modifier.testTag(ShowListTestTags.CREATE_LIST_BUTTON_TEST_TAG),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = state.labels.createListButtonText,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
internal fun ShowListContent(
    state: ShowListState,
    onAction: (ShowListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Column(modifier = modifier.fillMaxWidth()) {
        ShowListSheetTopBar(state = state, onAction = onAction)
        ShowListSheetContent(state = state, onAction = onAction)
    }
}
