package com.thomaskioko.tvmaniac.compose.components

import android.graphics.Color.parseColor
import android.graphics.Paint.Style
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.annotation.FontRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.thomaskioko.tvmaniac.compose.R
import com.thomaskioko.tvmaniac.compose.theme.listGradient

@Composable
fun GradientText(
    text: String,
    size: Float = 130F,
    textColorGradientList: List<Color> = listGradient,
    @FontRes fontResourceId: Int = R.font.work_sans_medium,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val paint = Paint().asFrameworkPaint()
    BoxWithConstraints {
        val y = size / 2
        val gradientShader: Shader = LinearGradientShader(
            from = Offset(0f, 0f),
            to = Offset(0F, size / 2),
            textColorGradientList
        )
        Canvas(modifier = modifier) {
            paint.apply {
                isAntiAlias = true
                textSize = size
                typeface = ResourcesCompat.getFont(context, fontResourceId)
                style = Style.FILL
                color = parseColor("#cdcdcd")
                xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
            }
            val x = -paint.measureText(text) / 2
            drawIntoCanvas { canvas ->
                canvas.save()
                canvas.nativeCanvas.drawText(text, x, y, paint)
                canvas.restore()
                paint.shader = gradientShader
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                paint.maskFilter = null
                canvas.nativeCanvas.drawText(text, x, y, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
                canvas.nativeCanvas.drawText(text, x, y, paint)
            }
            paint.reset()
        }
    }
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
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {

        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterStart),
            style = MaterialTheme.typography.h6
        )

        moreString?.let {
            Text(
                text = moreString, modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onMoreClicked() },
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
        text = text, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        style = MaterialTheme.typography.body2,
        textAlign = TextAlign.Center, color = MaterialTheme.colors.error
    )
}
