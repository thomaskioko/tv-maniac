package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme

@Composable
public fun BoxTextItems(
    title: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    onMoreClicked: () -> Unit = {},
) {
    Box(
        modifier = modifier,
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
public fun TextLoadingItem(
    title: String,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = Bold,
                ),
            )

            subTitle?.let {
                Text(
                    text = subTitle,
                    modifier = Modifier.padding(vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }
    }

    content()
}

@Composable
public fun ExpandingText(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    expandable: Boolean = true,
    collapsedMaxLines: Int = 4,
    expandedMaxLines: Int = Int.MAX_VALUE,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    var canTextExpand by remember(text) { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = text,
        style = textStyle,
        fontWeight = fontWeight,
        overflow = TextOverflow.Ellipsis,
        color = color,
        maxLines = if (expanded) expandedMaxLines else collapsedMaxLines,
        modifier = modifier.clickable(
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
private fun ExpandingTextPreview() {
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
private fun BoxTextItemsPreview() {
    TvManiacTheme {
        Surface {
            BoxTextItems(
                modifier = Modifier.fillMaxWidth(),
                title = "Being Watched",
                label = "More",
            )
        }
    }
}

@ThemePreviews
@Composable
private fun TextLoadingItemPreview() {
    TvManiacTheme {
        Surface {
            TextLoadingItem(
                title = "Seasons",
                content = {},
            )
        }
    }
}
