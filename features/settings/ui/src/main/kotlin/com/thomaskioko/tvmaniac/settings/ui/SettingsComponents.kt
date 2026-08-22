package com.thomaskioko.tvmaniac.settings.ui

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

private const val DISABLED_CONTENT_ALPHA = 0.38f

@Composable
internal fun SettingsSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.large, vertical = TvManiacSpacing.xSmall),
    )
}

@Composable
internal fun SettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.medium),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        ),
    ) {
        Column(content = content)
    }
}

@Composable
internal fun SettingsGroupDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = TvManiacSpacing.xxxLarge),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
    )
}

@Composable
internal fun SettingsNavigationRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    loadingTestTag: String? = null,
) {
    val contentAlpha = if (enabled || isLoading) 1f else DISABLED_CONTENT_ALPHA

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            SettingsIconChip(icon = icon, contentAlpha = contentAlpha)
            Spacer(modifier = Modifier.width(TvManiacSpacing.small))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                fontWeight = FontWeight.Medium,
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                )
            }
        }
        Spacer(modifier = Modifier.width(TvManiacSpacing.small))
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .then(if (loadingTestTag != null) Modifier.testTag(loadingTestTag) else Modifier),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
            )
        }
    }
}

@Composable
internal fun SettingsLinkRow(
    title: String,
    body: String,
    link: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.medium),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
    ) {
        if (leadingIcon != null) {
            Image(
                painter = leadingIcon,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = TvManiacSpacing.xxxSmall)
                    .size(40.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = link,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

internal fun openInCustomTab(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, url.toUri())
}

@Composable
private fun SettingsIconChip(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentAlpha: Float = 1f,
) {
    Surface(
        modifier = modifier.size(36.dp),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f * contentAlpha),
        shape = MaterialTheme.shapes.medium,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = contentAlpha),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
