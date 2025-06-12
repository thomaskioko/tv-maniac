package com.thomaskioko.tvmaniac.i18n.testing.util

import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

public actual fun StringResourceKey.getString(): String = resourceId.desc().localized()

public actual fun StringResourceKey.getString(vararg args: Any): String {
    return resourceId.format(*args).localized()
}

public actual fun PluralsResourceKey.getPlural(quantity: Int): String {
    return resourceId.format(quantity, quantity).localized()
}

public actual fun PluralsResourceKey.getPlural(quantity: Int, vararg args: Any): String {
    return resourceId.format(quantity, *args).localized()
}

public actual suspend fun StringDesc.getString(): String = localized()
