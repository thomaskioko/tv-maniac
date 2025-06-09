package com.thomaskioko.tvmaniac.i18n.testing.util

import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

public actual fun StringResourceKey.getString(): String = StringDesc.Resource(resourceId).localized()

public actual fun StringResourceKey.getString(vararg args: Any): String {
    return StringDesc.ResourceFormatted(resourceId, *args).toString()
}

public actual fun PluralsResourceKey.getPlural(quantity: Int): String {
    return StringDesc.Plural(resourceId, quantity).toString()
}

public actual fun PluralsResourceKey.getPlural(quantity: Int, vararg args: Any): String {
    return StringDesc.PluralFormatted(resourceId, quantity, *args).toString()
}
