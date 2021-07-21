package com.thomaskioko.tvmaniac.compose.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.R
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.elevatedSurface


@Composable
fun TvManiacTopBar(
    title: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable (() -> Unit) = { AppBarHomeIcon() }
) {
    TopAppBar(
        title = { Row { title() } },
        navigationIcon = navigationIcon,
        actions = actions,
        backgroundColor = MaterialTheme.colors.primary
    )
}

@Composable
fun AppBarScaffold(
    title: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable (() -> Unit) = { AppBarHomeIcon() },
    content: @Composable () -> Unit
) {

    val backgroundColor = MaterialTheme.colors.elevatedSurface(3.dp)

    Column(
        Modifier.background(backgroundColor.copy(alpha = 0.95f))
    ) {
        TvManiacScaffold(
            appBar = {
                TopAppBar(
                    title = { Row { title() } },
                    navigationIcon = navigationIcon,
                    actions = actions,
                    backgroundColor = MaterialTheme.colors.primary
                )
            },
            content = { content() }
        )
    }
}


@Composable
fun AppBarHomeIcon(onNavIconPressed: () -> Unit = { }) {
    Image(
        painter = painterResource(R.drawable.ic_tv_logo),
        contentDescription = null,
        modifier = Modifier
            .clickable(onClick = onNavIconPressed)
    )
}


@Preview(
    name = "AppBar",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "AppBar• Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun AppBarDarkPreview() {
    TvManiacTheme {
        TvManiacTopBar(title = { Text("Tv Maniac") })
    }
}

@Preview(
    name = "AppBar Settings",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "AppBar Settings• Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)


@Preview("AppBar Scaffold")
@Composable
private fun AppBarScaffoldPreview() {
    TvManiacTheme {
        AppBarScaffold(title = { Text("Tv Maniac") }, content = {})
    }
}
