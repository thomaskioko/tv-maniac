package com.thomaskioko.tvmaniac.ratingsheet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_action_remove_rating
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_rating_sheet_title
import com.thomaskioko.tvmaniac.ratingsheet.presenter.RatingSheetAction
import com.thomaskioko.tvmaniac.ratingsheet.presenter.RatingSheetPresenter
import com.thomaskioko.tvmaniac.ratingsheet.presenter.RatingSheetState
import com.thomaskioko.tvmaniac.testtags.ratingsheet.RatingSheetTestTags
import io.github.thomaskioko.codegen.annotations.SheetUi

@SheetUi(presenter = RatingSheetPresenter::class, parentScope = ActivityScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun RatingSheet(
    presenter: RatingSheetPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { presenter.dispatch(RatingSheetAction.Dismissed) },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        modifier = modifier,
    ) {
        RatingSheetContent(
            state = state,
            onAction = presenter::dispatch,
        )
    }
}

@Composable
internal fun RatingSheetContent(
    state: RatingSheetState,
    onAction: (RatingSheetAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(RatingSheetTestTags.SHEET_TEST_TAG)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(label_rating_sheet_title.resourceId),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        val userRating = state.userRating

        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (star in 1..STAR_COUNT) {
                val value = star * POINTS_PER_STAR
                RatingStar(
                    value = value,
                    userRating = userRating,
                    onClick = { onAction(RatingSheetAction.RatingSelected(value)) },
                )
            }
        }

        if (userRating != null) {
            RemoveRatingRow(
                modifier = Modifier.padding(top = 16.dp),
                onClick = { onAction(RatingSheetAction.RatingCleared) },
            )
        }
    }
}

@Composable
private fun RatingStar(
    value: Int,
    userRating: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val filled = userRating != null && userRating >= value
    val half = userRating != null && userRating == value - 1
    Box(
        modifier = modifier
            .size(40.dp)
            .testTag(RatingSheetTestTags.starRating(value))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = when {
                filled -> Icons.Filled.Star
                half -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            },
            contentDescription = value.toString(),
            modifier = Modifier.size(32.dp),
            tint = if (filled || half) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RemoveRatingRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .testTag(RatingSheetTestTags.CLEAR_RATING_BUTTON)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.DeleteOutline,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.error,
        )

        Text(
            text = stringResource(label_action_remove_rating.resourceId),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RatingSheetContentUnratedPreview() {
    RatingSheetContent(
        state = RatingSheetState(userRating = null),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RatingSheetContentRatedPreview() {
    RatingSheetContent(
        state = RatingSheetState(userRating = 8),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RatingSheetContentHalfRatedPreview() {
    RatingSheetContent(
        state = RatingSheetState(userRating = 7),
        onAction = {},
    )
}

private const val STAR_COUNT = 5
private const val POINTS_PER_STAR = 2
