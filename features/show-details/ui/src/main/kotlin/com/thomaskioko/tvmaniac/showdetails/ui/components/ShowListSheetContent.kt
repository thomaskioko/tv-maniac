package com.thomaskioko.tvmaniac.showdetails.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.FilledTextButton
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.presenter.showdetails.CreateListSubmitted
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsContent
import com.thomaskioko.tvmaniac.presenter.showdetails.ToggleShowInList
import com.thomaskioko.tvmaniac.presenter.showdetails.UpdateCreateListName
import com.thomaskioko.tvmaniac.showdetails.ui.showDetailsWithCreateFieldExpanded
import com.thomaskioko.tvmaniac.showdetails.ui.showDetailsWithCreateListLoading
import com.thomaskioko.tvmaniac.showdetails.ui.showDetailsWithEmptyTraktLists
import com.thomaskioko.tvmaniac.showdetails.ui.showDetailsWithTraktLists
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags

// TODO:: Move this to a feature module user-show-list
@Composable
internal fun ShowListSheetContent(
    state: ShowDetailsContent,
    onAction: (ShowDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(ShowDetailsTestTags.LIST_SHEET_TEST_TAG)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        val title = state.showDetails.title

        PosterCard(
            imageUrl = state.showDetails.posterImageUrl,
            title = title,
            imageWidth = 150.dp,
            shape = MaterialTheme.shapes.medium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = state.listsHeaderText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.traktLists.isEmpty()) {
            EmptyListContent(state)
        } else {
            TraktListItems(state, onAction)
        }

        Spacer(modifier = Modifier.height(16.dp))

        CreateListInlineField(state, onAction)
    }
}

@Composable
private fun TraktListItems(
    state: ShowDetailsContent,
    onAction: (ShowDetailsAction) -> Unit,
) {
    state.traktLists.forEach { list ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(ShowDetailsTestTags.traktListItem(list.id))
                .padding(vertical = 4.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = list.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = list.showCountText,
                        modifier = Modifier.testTag(ShowDetailsTestTags.traktListItemShowCount(list.id)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Switch(
                    checked = list.isShowInList,
                    onCheckedChange = {
                        onAction(ToggleShowInList(listId = list.id, isCurrentlyInList = list.isShowInList))
                    },
                    modifier = Modifier.testTag(ShowDetailsTestTags.traktListItemSwitch(list.id)),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                )
            }
        }
    }
}

@Composable
private fun EmptyListContent(
    state: ShowDetailsContent,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = state.emptyListText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CreateListInlineField(
    state: ShowDetailsContent,
    onAction: (ShowDetailsAction) -> Unit,
) {
    AnimatedVisibility(visible = state.showCreateListField) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = state.createListName,
                onValueChange = { if (it.length <= 50) onAction(UpdateCreateListName(it)) },
                modifier = Modifier
                    .weight(1f)
                    .testTag(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG),
                placeholder = {
                    Text(
                        text = state.createListPlaceholder,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        ),
                    )
                },
                singleLine = true,
                enabled = !state.isCreatingList,
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                ),
            )

            if (state.isCreatingList) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .testTag(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_PROGRESS_TEST_TAG),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            } else {
                FilledTextButton(
                    onClick = { onAction(CreateListSubmitted) },
                    modifier = Modifier.testTag(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_SUBMIT_TEST_TAG),
                    enabled = state.createListName.isNotBlank(),
                    buttonColors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(state.createListDoneText)
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowListSheetContentPreview(
    @PreviewParameter(ShowListSheetPreviewParameterProvider::class) state: ShowDetailsContent,
) {
    ShowListSheetContent(
        state = state,
        onAction = {},
    )
}

private class ShowListSheetPreviewParameterProvider : PreviewParameterProvider<ShowDetailsContent> {
    override val values: Sequence<ShowDetailsContent>
        get() = sequenceOf(
            showDetailsWithTraktLists,
            showDetailsWithEmptyTraktLists,
            showDetailsWithCreateFieldExpanded,
            showDetailsWithCreateListLoading,
        )
}
