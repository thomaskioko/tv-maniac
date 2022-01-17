package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.thomaskioko.tvmaniac.resources.R

private val workSansFontFamily = FontFamily(
    fonts = listOf(
        Font(
            resId = R.font.work_sans_thin,
            weight = FontWeight.W200,
            style = FontStyle.Normal
        ),
        Font(
            resId = R.font.work_sans_medium,
            weight = FontWeight.W400,
            style = FontStyle.Normal
        ),
        Font(
            resId = R.font.work_sans_semibold,
            weight = FontWeight.W500,
            style = FontStyle.Normal
        ),
        Font(
            resId = R.font.work_sans_bold,
            weight = FontWeight.W600,
            style = FontStyle.Normal
        ),
        Font(
            resId = R.font.work_sans_extrabold,
            weight = FontWeight.W700,
            style = FontStyle.Normal
        ),

    )
)

val TvManiacTypography = Typography(
    h4 = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 30.sp
    ),
    h5 = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 20.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = workSansFontFamily,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp
    )
)
