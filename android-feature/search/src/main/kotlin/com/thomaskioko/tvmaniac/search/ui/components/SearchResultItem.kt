package com.thomaskioko.tvmaniac.search.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_clear_text
import com.thomaskioko.tvmaniac.i18n.resolve

@Composable
internal fun SearchResultItem(
    imageUrl: String?,
    title: String,
    year: String?,
    status: String?,
    voteAverage: Double?,
    overview: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PosterCard(
                imageUrl = imageUrl,
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(0.8f),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp),
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 4.dp),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    val divider = buildAnnotatedString {
                        val tagStyle = MaterialTheme.typography.labelSmall
                            .toSpanStyle()
                            .copy(
                                fontSize = 8.sp,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        withStyle(tagStyle) { append("  •  ") }
                    }

                    val tagStyle = MaterialTheme.typography.bodySmall
                        .toSpanStyle()
                        .copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                    val text = buildAnnotatedString {
                        val statusStyle = MaterialTheme.typography.labelMedium
                            .toSpanStyle()
                            .copy(
                                color = MaterialTheme.colorScheme.secondary,
                                background = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                                fontWeight = FontWeight.Normal,
                            )

                        voteAverage?.let {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Outlined.StarOutline,
                                contentDescription = cd_clear_text.resolve(LocalContext.current),
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                            withStyle(tagStyle) { append("$it") }
                            append(divider)
                        }

                        year?.let {
                            withStyle(tagStyle) { append(it) }
                            append(divider)
                        }

                        status?.let {
                            withStyle(statusStyle) {
                                append(" ")
                                append(it)
                                append(" ")
                            }
                        }
                    }

                    Text(
                        text = text,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }

                overview?.let {
                    Text(
                        text = overview,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun SearchResultItemPreview() {
    TvManiacTheme {
        Surface {
            SearchResultItem(
                title = "Loki",
                status = "Ended",
                year = "2012",
                voteAverage = 6.7,
                overview = "After stealing the Tesseract during the events of “Avengers: Endgame,”. " +
                    "After stealing the Tesseract during the events of “Avengers: Endgame. After stealing the Tesseract during the events of “Avengers: Endgame",
                imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                onClick = {},
            )
        }
    }
}
