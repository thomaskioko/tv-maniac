package com.thomaskioko.tvmaniac.i18n.testing.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

public actual fun StringResourceKey.getString(): String {
    val context: Context = ApplicationProvider.getApplicationContext()
    return resourceId.desc().toString(context)
}

public actual fun StringResourceKey.getString(vararg args: Any): String {
    val context: Context = ApplicationProvider.getApplicationContext()
    return resourceId.format(*args).toString(context)
}

public actual fun PluralsResourceKey.getPlural(quantity: Int): String {
    val context: Context = ApplicationProvider.getApplicationContext()
    return resourceId.format(quantity, quantity).toString(context)
}

public actual fun PluralsResourceKey.getPlural(quantity: Int, vararg args: Any): String {
    val context: Context = ApplicationProvider.getApplicationContext()
    return resourceId.format(quantity, *args).toString(context)
}

public actual suspend fun StringDesc.getString(): String {
    val context: Context = ApplicationProvider.getApplicationContext()
    return toString(context)
}
