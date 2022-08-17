package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun H6(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = MaterialTheme.typography.h6, modifier = modifier)
}

@Composable
fun BoxTextItems(
    title: String,
    moreString: String? = null,
    onMoreClicked: () -> Unit = { }
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {

        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterStart),
            style = MaterialTheme.typography.h6
        )

        moreString?.let {
            Text(
                text = moreString,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onMoreClicked() }
                    .padding(16.dp),
                style = MaterialTheme.typography.overline.copy(
                    color = MaterialTheme.colors.secondary
                )
            )
        }
    }
}

@Composable
fun ErrorText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        style = MaterialTheme.typography.body2,
        textAlign = TextAlign.Center, color = MaterialTheme.colors.error
    )
}
