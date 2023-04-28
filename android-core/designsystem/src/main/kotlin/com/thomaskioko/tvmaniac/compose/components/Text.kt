package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme

@Composable
fun BoxTextItems(
    title: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    onMoreClicked: () -> Unit = { },
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterStart),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = Bold,
            ),
        )

        label?.let {
            Text(
                text = label,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onMoreClicked() }
                    .padding(16.dp),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.secondary,
                ),
            )
        }
    }
}

@Composable
fun TextLoadingItem(
    isLoading: Boolean,
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.CenterStart),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = Bold,
            ),
        )

        AnimatedVisibility(
            visible = isLoading,
            modifier = Modifier
                .align(Alignment.CenterEnd),
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
fun ExpandingText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    expandable: Boolean = true,
    collapsedMaxLines: Int = 4,
    expandedMaxLines: Int = Int.MAX_VALUE,
) {
    var canTextExpand by remember(text) { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = text,
        style = textStyle,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = if (expanded) expandedMaxLines else collapsedMaxLines,
        modifier = modifier
            .clickable(
                enabled = expandable && canTextExpand,
                onClick = { expanded = !expanded },
            ),
        onTextLayout = {
            if (!expanded) {
                canTextExpand = it.hasVisualOverflow
            }
        },
    )
}

@ThemePreviews
@Composable
fun ExpandingTextPreview() {
    TvManiacTheme {
        Surface {
            ExpandingText(
                text = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                    "an alternate version of Loki is brought to the mysterious Time Variance " +
                    "Authority, a bureaucratic organization that exists outside of time and " +
                    "space and monitors the timeline. They give Loki a choice: face being " +
                    "erased from existence due to being a “time variant”or help fix " +
                    "the timeline and stop a greater threat.",
            )
        }
    }
}

@ThemePreviews
@Composable
fun BoxTextItemsPreview() {
    TvManiacTheme {
        Surface {
            BoxTextItems(
                title = "Being Watched",
                label = "More",
            )
        }
    }
}

@ThemePreviews
@Composable
fun TextLoadingItemPreview() {
    TvManiacTheme {
        Surface {
            TextLoadingItem(
                text = "Seasons",
                isLoading = true,
            )
        }
    }
}
