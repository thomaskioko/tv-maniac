package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.thomaskioko.tvmaniac.compose.util.LocalHapticFeedbackEnabled

/**
 * Clickable [Row] that owns the haptic pulse for its long-press affordance, so call sites pass plain
 * action lambdas instead of wiring haptics themselves. Plain taps stay silent (rows usually
 * navigate); [onLongClick] fires a gated pulse before running.
 *
 * [hapticEnabled] gates the pulse and defaults to the ambient [LocalHapticFeedbackEnabled].
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun HapticRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    hapticEnabled: Boolean = LocalHapticFeedbackEnabled.current,
    content: @Composable RowScope.() -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick?.let { longClick ->
                {
                    if (hapticEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    longClick()
                }
            },
        ),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
        content = content,
    )
}
