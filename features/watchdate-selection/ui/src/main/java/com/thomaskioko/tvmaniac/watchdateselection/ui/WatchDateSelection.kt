package com.thomaskioko.tvmaniac.watchdateselection.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.SheetActionItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.testtags.watchdateselection.WatchDateSelectionTestTags
import com.thomaskioko.tvmaniac.watchdateselection.presenter.WatchDateSelectionAction
import com.thomaskioko.tvmaniac.watchdateselection.presenter.WatchDateSelectionPresenter
import com.thomaskioko.tvmaniac.watchdateselection.presenter.WatchDateSelectionState
import io.github.thomaskioko.codegen.annotations.SheetUi
import kotlinx.datetime.LocalDate

@SheetUi(presenter = WatchDateSelectionPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun WatchDateSelection(
    presenter: WatchDateSelectionPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { presenter.dispatch(WatchDateSelectionAction.Dismissed) },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = sheetShape,
        dragHandle = null,
        modifier = modifier,
    ) {
        WatchDateSelectionContent(
            state = state,
            onAction = presenter::dispatch,
        )
    }
}

@Composable
internal fun WatchDateSelectionContent(
    state: WatchDateSelectionState,
    onAction: (WatchDateSelectionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(sheetShape)
            .background(MaterialTheme.colorScheme.surface)
            .testTag(WatchDateSelectionTestTags.SHEET_TEST_TAG),
    ) {
        SheetGrabber()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium)
                .padding(top = TvManiacSpacing.xSmall, bottom = TvManiacSpacing.small),
            verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        ) {
            Text(
                text = state.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            val currentWatchedAtLabel = state.currentWatchedAtLabel
            if (currentWatchedAtLabel != null) {
                Text(
                    text = currentWatchedAtLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        SheetActionItem(
            icon = Icons.Outlined.Check,
            label = state.justNowLabel,
            onClick = { onAction(WatchDateSelectionAction.JustNowSelected) },
            modifier = Modifier.testTag(WatchDateSelectionTestTags.JUST_NOW),
        )

        SheetActionItem(
            icon = Icons.Outlined.Schedule,
            label = state.releaseDateLabel,
            onClick = { onAction(WatchDateSelectionAction.ReleaseDateSelected) },
            modifier = Modifier.testTag(WatchDateSelectionTestTags.RELEASE_DATE),
            enabled = state.isReleaseDateEnabled,
        )

        SheetActionItem(
            icon = Icons.Outlined.CalendarMonth,
            label = state.otherDateLabel,
            onClick = { showDatePicker = true },
            modifier = Modifier.testTag(WatchDateSelectionTestTags.OTHER_DATE),
        )

        SheetActionItem(
            icon = Icons.Outlined.HelpOutline,
            label = state.unknownDateLabel,
            onClick = { onAction(WatchDateSelectionAction.UnknownDateSelected) },
            modifier = Modifier.testTag(WatchDateSelectionTestTags.UNKNOWN_DATE),
        )

        Spacer(modifier = Modifier.height(TvManiacSpacing.large))
    }

    if (showDatePicker) {
        WatchDatePickerDialog(
            maxSelectableDate = state.maxSelectableDate,
            onDismiss = { showDatePicker = false },
            onDateChosen = { date ->
                showDatePicker = false
                pickedDate = date
            },
        )
    }

    pickedDate?.let { date ->
        WatchTimePickerDialog(
            onDismiss = { pickedDate = null },
            onTimeChosen = { time ->
                pickedDate = null
                onAction(WatchDateSelectionAction.OtherDateSelected(date = date, time = time))
            },
        )
    }
}

@Composable
private fun SheetGrabber(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = TvManiacSpacing.small),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)),
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun WatchDateSelectionDefaultPreview() {
    WatchDateSelectionContent(state = previewState(), onAction = {})
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun WatchDateSelectionNoReleaseDatePreview() {
    WatchDateSelectionContent(state = previewState(isReleaseDateEnabled = false), onAction = {})
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun WatchDateSelectionEditPreview() {
    WatchDateSelectionContent(
        state = previewState(title = "Change watched date", currentWatchedAtLabel = "12 Jan 2026 20:30"),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun WatchDateSelectionEditUnknownPreview() {
    WatchDateSelectionContent(
        state = previewState(title = "Change watched date", currentWatchedAtLabel = "A long time ago"),
        onAction = {},
    )
}

private fun previewState(
    title: String = "When did you watch this?",
    isReleaseDateEnabled: Boolean = true,
    currentWatchedAtLabel: String? = null,
) = WatchDateSelectionState(
    title = title,
    justNowLabel = "Just now",
    releaseDateLabel = "Release date",
    otherDateLabel = "Other date…",
    unknownDateLabel = "Unknown date",
    isReleaseDateEnabled = isReleaseDateEnabled,
    currentWatchedAtLabel = currentWatchedAtLabel,
    maxSelectableDate = LocalDate(2026, 8, 16),
)

private val sheetShape: CornerBasedShape
    @Composable get() = MaterialTheme.shapes.large.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
