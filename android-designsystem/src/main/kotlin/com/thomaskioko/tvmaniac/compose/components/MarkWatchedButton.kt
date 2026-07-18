package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.util.LocalHapticFeedbackEnabled

@Composable
public fun MarkWatchedButton(
    isWatched: Boolean,
    isUpdating: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    hapticEnabled: Boolean = LocalHapticFeedbackEnabled.current,
) {
    val haptics = LocalHapticFeedback.current
    Surface(
        onClick = {
            if (hapticEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onToggle()
        },
        enabled = !isUpdating,
        modifier = modifier.size(28.dp),
        shape = CircleShape,
        color = if (isWatched) TvManiacTheme.colorScheme.success else TvManiacTheme.colorScheme.grey,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isUpdating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}
