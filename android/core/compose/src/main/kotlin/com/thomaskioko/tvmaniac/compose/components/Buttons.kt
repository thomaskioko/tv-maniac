package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun ExtendedFab(
    painter: Painter,
    text: String,
    onClick: () -> Unit = {}
) {
    ExtendedFloatingActionButton(
        icon = {
            Image(
                painter = painter,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary.copy(alpha = 0.8F)),
            )
        },
        text = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.body2,
                )
            }
        },
        shape = RectangleShape,
        backgroundColor = Color.Transparent,
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        onClick = { onClick() },
        modifier = Modifier
            .padding(2.dp)
            .border(1.dp, Color(0xFF414141), RoundedCornerShape(8.dp))
    )
}

@Composable
fun UserProfileButton(
    loggedIn: Boolean,
    avatarUrl: String?,
    fullName: String?,
    userName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        when {
            loggedIn && avatarUrl != null -> {
                NetworkImageComposable(
                    imageUrl = avatarUrl,
                    contentDescription = stringResource(
                        R.string.cd_profile_pic, fullName ?: userName
                    ),
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colors.secondary, CircleShape)
                )
            }
            else -> {
                Icon(
                    imageVector = when {
                        loggedIn -> Icons.Default.Person
                        else -> Icons.Outlined.Person
                    },
                    contentDescription = stringResource(R.string.cd_user_profile)
                )
            }
        }
    }
}
