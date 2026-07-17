package com.thomaskioko.tvmaniac.showlist.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.android.designsystem.R
import com.thomaskioko.tvmaniac.compose.components.FilledTextButton
import com.thomaskioko.tvmaniac.compose.components.ProviderButton
import com.thomaskioko.tvmaniac.compose.components.ProviderSignInCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSwitch
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.presentation.showlist.ShowListAction
import com.thomaskioko.tvmaniac.presentation.showlist.ShowListState
import com.thomaskioko.tvmaniac.testtags.showlist.ShowListTestTags

@Composable
internal fun ShowListSheetContent(
    state: ShowListState,
    onAction: (ShowListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(ShowListTestTags.SHEET_TEST_TAG)
            .padding(horizontal = TvManiacSpacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!state.isLoggedIn) {
            LoginRequiredContent(state, onAction)
            return@Column
        }

        Spacer(modifier = Modifier.height(TvManiacSpacing.xSmall))

        Text(
            text = state.labels.listsHeaderText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.xSmall),
        )

        Spacer(modifier = Modifier.height(TvManiacSpacing.xSmall))

        when {
            state.isLoading -> LoadingContent()
            state.traktLists.isEmpty() -> EmptyListContent(state)
            else -> TraktListItems(state, onAction)
        }

        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

        CreateListInlineField(state, onAction)
    }
}

@Composable
private fun TraktListItems(
    state: ShowListState,
    onAction: (ShowListAction) -> Unit,
) {
    state.traktLists.forEach { list ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(ShowListTestTags.traktListItem(list.id))
                .padding(vertical = TvManiacSpacing.xxSmall),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = TvManiacSpacing.xSmall, vertical = TvManiacSpacing.small),
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
                        modifier = Modifier.testTag(ShowListTestTags.traktListItemShowCount(list.id)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (list.isToggling) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .testTag(ShowListTestTags.traktListItemProgress(list.id)),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                } else {
                    TvManiacSwitch(
                        checked = list.isShowInList,
                        onCheckedChange = {
                            onAction(
                                ShowListAction.ToggleShowInList(
                                    listId = list.id,
                                    isCurrentlyInList = list.isShowInList,
                                ),
                            )
                        },
                        modifier = Modifier.testTag(ShowListTestTags.traktListItemSwitch(list.id)),
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = TvManiacSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(28.dp)
                .testTag(ShowListTestTags.LOADING_INDICATOR_TEST_TAG),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun EmptyListContent(state: ShowListState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = TvManiacSpacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = state.labels.emptyListText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CreateListInlineField(
    state: ShowListState,
    onAction: (ShowListAction) -> Unit,
) {
    AnimatedVisibility(visible = state.showCreateListField) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        ) {
            OutlinedTextField(
                value = state.createListName,
                onValueChange = {
                    if (it.length <= MAX_LIST_NAME_LENGTH) {
                        onAction(ShowListAction.UpdateCreateListName(it))
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag(ShowListTestTags.CREATE_LIST_INPUT_TEST_TAG),
                placeholder = {
                    Text(
                        text = state.labels.createListPlaceholder,
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
                        .testTag(ShowListTestTags.CREATE_LIST_PROGRESS_TEST_TAG),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            } else {
                FilledTextButton(
                    onClick = { onAction(ShowListAction.CreateListSubmitted) },
                    modifier = Modifier.testTag(ShowListTestTags.CREATE_LIST_SUBMIT_TEST_TAG),
                    enabled = state.createListName.isNotBlank(),
                    buttonColors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(state.labels.createListDoneText)
                }
            }
        }
    }
}

@Composable
private fun LoginRequiredContent(
    state: ShowListState,
    onAction: (ShowListAction) -> Unit,
) {
    ProviderSignInCard(
        title = state.labels.loginRequiredTitle,
        description = state.labels.loginRequiredMessage,
        modifier = Modifier.padding(vertical = TvManiacSpacing.large),
    ) {
        state.authProviders.forEach { option ->
            ProviderButton(
                text = option.label,
                logo = providerLogo(option.provider),
                onClick = { onAction(ShowListAction.Login(option.provider)) },
                modifier = if (option.provider == SyncProviderSource.TRAKT) {
                    Modifier.testTag(ShowListTestTags.LOGIN_REQUIRED_CONFIRM_BUTTON_TEST_TAG)
                } else {
                    Modifier
                },
            )
        }
    }
}

@DrawableRes
private fun providerLogo(provider: SyncProviderSource): Int = when (provider) {
    SyncProviderSource.TRAKT -> R.drawable.ic_trakt_mono
    SyncProviderSource.SIMKL -> R.drawable.ic_simkl_mono
}

private const val MAX_LIST_NAME_LENGTH = 50

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowListSheetContentPreview(
    @PreviewParameter(ShowListPreviewParameterProvider::class) state: ShowListState,
) {
    ShowListSheetContent(
        state = state,
        onAction = {},
    )
}
