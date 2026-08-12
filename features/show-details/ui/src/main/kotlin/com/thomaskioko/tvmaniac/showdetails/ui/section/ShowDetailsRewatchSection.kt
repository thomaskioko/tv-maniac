package com.thomaskioko.tvmaniac.showdetails.ui.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.FilledTextButton
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR
import com.thomaskioko.tvmaniac.presenter.showdetails.header.FinishRewatchClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderAction
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderState
import com.thomaskioko.tvmaniac.presenter.showdetails.header.StartRewatchClicked
import com.thomaskioko.tvmaniac.showdetails.ui.previewHeaderState
import com.thomaskioko.tvmaniac.showdetails.ui.previewHeaderStateRewatchFinished
import com.thomaskioko.tvmaniac.showdetails.ui.previewHeaderStateRewatchInProgress
import com.thomaskioko.tvmaniac.showdetails.ui.previewHeaderStateRewatchSimklFreeTier
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags

@Composable
internal fun ShowDetailsRewatchSection(
    state: ShowDetailsHeaderState,
    onAction: (ShowDetailsHeaderAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 1.dp),
    ) {
        if (state.isInLibrary) {
            RewatchCard(state = state, onAction = onAction)
        }
    }
}

@Composable
private fun RewatchCard(
    state: ShowDetailsHeaderState,
    onAction: (ShowDetailsHeaderAction) -> Unit,
) {
    Column(modifier = Modifier.testTag(ShowDetailsTestTags.REWATCH_SECTION_TEST_TAG)) {
        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = TvManiacElevation.small),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(TvManiacSpacing.medium),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.padding(end = TvManiacSpacing.small)) {
                        RewatchTitleText(
                            isRewatching = state.isRewatching,
                            rewatchCount = state.rewatchCount,
                        )

                        if (state.isRewatching) {
                            RewatchProgressText(
                                watchedEpisodes = state.rewatchWatchedEpisodes,
                                airedEpisodes = state.rewatchAiredEpisodes,
                            )
                        }
                    }

                    FilledTextButton(
                        modifier = Modifier.testTag(ShowDetailsTestTags.REWATCH_ACTION_BUTTON_TEST_TAG),
                        shape = MaterialTheme.shapes.medium,
                        buttonColors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            containerColor = MaterialTheme.colorScheme.secondary,
                        ),
                        onClick = {
                            if (state.isRewatching) {
                                onAction(FinishRewatchClicked)
                            } else {
                                onAction(StartRewatchClicked)
                            }
                        },
                        content = {
                            Icon(
                                imageVector = if (state.isRewatching) {
                                    Icons.Filled.Stop
                                } else {
                                    Icons.Filled.Replay
                                },
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                            )
                            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                            Text(
                                text = state.rewatchActionLabel,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                    )
                }

                state.rewatchSyncNotice?.let { notice ->
                    Spacer(modifier = Modifier.height(TvManiacSpacing.small))

                    Text(
                        text = notice,
                        modifier = Modifier.testTag(ShowDetailsTestTags.REWATCH_SYNC_NOTICE_TEST_TAG),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RewatchTitleText(isRewatching: Boolean, rewatchCount: Int) {
    Text(
        text = when {
            isRewatching -> stringResource(MR.strings.label_rewatch_in_progress.resourceId)
            rewatchCount > 0 -> pluralStringResource(
                MR.plurals.rewatch_count.resourceId,
                rewatchCount,
                rewatchCount,
            )
            else -> stringResource(MR.strings.label_rewatch_none.resourceId)
        },
        modifier = Modifier.testTag(ShowDetailsTestTags.REWATCH_COUNT_TEST_TAG),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun RewatchProgressText(watchedEpisodes: Int, airedEpisodes: Int) {
    if (airedEpisodes <= 0) return

    Text(
        text = pluralStringResource(
            MR.plurals.episodes_watched.resourceId,
            airedEpisodes,
            watchedEpisodes,
            airedEpisodes,
        ),
        modifier = Modifier.testTag(ShowDetailsTestTags.REWATCH_PROGRESS_TEST_TAG),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsRewatchSectionPreview() {
    ShowDetailsRewatchSection(state = previewHeaderState, onAction = {})
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsRewatchSectionInProgressPreview() {
    ShowDetailsRewatchSection(state = previewHeaderStateRewatchInProgress, onAction = {})
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsRewatchSectionFinishedPreview() {
    ShowDetailsRewatchSection(state = previewHeaderStateRewatchFinished, onAction = {})
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsRewatchSectionSimklFreeTierPreview() {
    ShowDetailsRewatchSection(state = previewHeaderStateRewatchSimklFreeTier, onAction = {})
}
