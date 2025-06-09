package com.thomaskioko.tvmaniac.i18n.testing.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

public actual fun StringResourceKey.getString(): String {
    val context: Context = ApplicationProvider.getApplicationContext()
    return StringDesc.Resource(resourceId).toString(context)
}

public actual fun StringResourceKey.getString(vararg args: Any): String {
    val context: Context = ApplicationProvider.getApplicationContext()
    return StringDesc.ResourceFormatted(resourceId, *args).toString(context)
}

public actual fun PluralsResourceKey.getPlural(quantity: Int): String {
    val context: Context = ApplicationProvider.getApplicationContext()
    return StringDesc.Plural(resourceId, quantity).toString(context)
}

public actual fun PluralsResourceKey.getPlural(quantity: Int, vararg args: Any): String {
    val context: Context = ApplicationProvider.getApplicationContext()
    return StringDesc.PluralFormatted(resourceId, quantity, *args).toString(context)
}
