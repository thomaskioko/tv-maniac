package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.util.LocalHapticFeedbackEnabled

@Composable
public fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
    locked: Boolean = false,
    lockedBadgeText: String = "",
    hapticEnabled: Boolean = LocalHapticFeedbackEnabled.current,
) {
    val haptics = LocalHapticFeedback.current
    val onToggle: (Boolean) -> Unit = { value ->
        if (hapticEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        onCheckedChange(value)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                enabled = !locked,
                onValueChange = onToggle,
                role = Role.Switch,
            )
            .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            SwitchRowIconChip(icon = icon)
            Spacer(modifier = Modifier.width(TvManiacSpacing.small))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
            )
            if (locked) {
                Spacer(modifier = Modifier.height(TvManiacSpacing.xxSmall))
                PremiumBadge(text = lockedBadgeText)
                Spacer(modifier = Modifier.height(TvManiacSpacing.xxSmall))
            }
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.width(TvManiacSpacing.small))
        TvManiacSwitch(
            checked = checked,
            onCheckedChange = onToggle,
            enabled = !locked,
        )
    }
}

@Composable
private fun SwitchRowIconChip(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(36.dp),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
        shape = MaterialTheme.shapes.medium,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
