package com.thomaskioko.tvmaniac.compose.components

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import com.thomaskioko.tvmaniac.compose.R
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.elevatedSurface
import com.thomaskioko.tvmaniac.compose.util.iconButtonBackgroundScrim


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
fun BackAppBar(
    title: String,
    onBackClick: () -> Unit
) {

    TopAppBar(
        title = { H6(text = title) },
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.ic_baseline_arrow_back_24),
                contentDescription = null,
                modifier = Modifier
                    .clickable(onClick = onBackClick)
                    .padding(16.dp)
            )
        },
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.95f),
        elevation = 3.dp
    )
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

@Composable
fun CollapsableAppBar(
    title: String?,
    showAppBarBackground: Boolean,
    onNavIconPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val backgroundColor by animateColorAsState(
        targetValue = when {
            showAppBarBackground -> MaterialTheme.colors.surface
            else -> Color.Transparent
        },
        animationSpec = spring(),
    )

    val elevation by animateDpAsState(
        targetValue = when {
            showAppBarBackground -> 4.dp
            else -> 0.dp
        },
        animationSpec = spring(),
    )

    TopAppBar(
        title = {
            Crossfade(showAppBarBackground && title != null) { show ->
                if (show) Text(text = title!!)
            }
        },
        contentPadding = rememberInsetsPaddingValues(
            LocalWindowInsets.current.systemBars,
            applyBottom = false
        ),
        navigationIcon = {
            IconButton(
                onClick = onNavIconPressed,
                modifier = Modifier.iconButtonBackgroundScrim(enabled = !showAppBarBackground),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.cd_navigate_back),
                )
            }
        },
        elevation = elevation,
        backgroundColor = backgroundColor,
        modifier = modifier
    )
}


@Preview(
    name = "BackAppBar",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "BackAppBar• Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun BackAppBarPreview() {
    TvManiacTheme {
        BackAppBar(title = "Tv Maniac", onBackClick = {})
    }
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
