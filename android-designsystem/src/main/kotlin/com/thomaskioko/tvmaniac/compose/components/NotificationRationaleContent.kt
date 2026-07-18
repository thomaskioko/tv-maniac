package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR.strings.notification_rationale_enable
import com.thomaskioko.tvmaniac.i18n.MR.strings.notification_rationale_message
import com.thomaskioko.tvmaniac.i18n.MR.strings.notification_rationale_not_now
import com.thomaskioko.tvmaniac.i18n.MR.strings.notification_rationale_title
import com.thomaskioko.tvmaniac.testtags.notifications.NotificationRationaleTestTags
import dev.icerock.moko.resources.compose.stringResource

@Composable
public fun NotificationRationaleContent(
    onEnable: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .testTag(NotificationRationaleTestTags.BOTTOM_SHEET)
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.large, vertical = TvManiacSpacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.secondary,
        )

        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

        Text(
            text = stringResource(notification_rationale_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(TvManiacSpacing.xSmall))

        Text(
            text = stringResource(notification_rationale_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

        EpisodeDateSection()

        Spacer(modifier = Modifier.height(TvManiacSpacing.large))

        Button(
            onClick = onEnable,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(NotificationRationaleTestTags.ENABLE_BUTTON),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
            shape = MaterialTheme.shapes.small,
        ) {
            Text(text = stringResource(notification_rationale_enable))
        }

        TextButton(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(NotificationRationaleTestTags.DISMISS_BUTTON),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary,
            ),
        ) {
            Text(text = stringResource(notification_rationale_not_now))
        }

        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))
    }
}

@Composable
private fun EpisodeDateSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        GradientDivider()

        Spacer(modifier = Modifier.height(TvManiacSpacing.small))

        Row(
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(12, 13, 14).forEach { day ->
                Text(
                    text = "$day",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(56.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.medium,
                    ),
            ) {
                Text(
                    text = "15",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "FEB",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            listOf(16, 17, 18).forEach { day ->
                Text(
                    text = "$day",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(TvManiacSpacing.small))

        GradientDivider()
    }
}

@Composable
private fun GradientDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.large)
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        Color.Transparent,
                    ),
                ),
            ),
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun NotificationRationaleContentPreview() {
    NotificationRationaleContent(
        onEnable = {},
        onDismiss = {},
    )
}
