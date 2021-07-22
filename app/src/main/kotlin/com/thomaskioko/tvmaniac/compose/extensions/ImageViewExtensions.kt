package com.thomaskioko.tvmaniac.compose.extensions

import android.widget.ImageView
import com.thomaskioko.tvmaniac.util.GlideApp

fun ImageView.load(url: String?) {
    url?.let {
        if (it.trim().isNotEmpty()) {
            //TODO Replace with Coil
            GlideApp.with(this).load(it).into(this)
        }
    }
}
