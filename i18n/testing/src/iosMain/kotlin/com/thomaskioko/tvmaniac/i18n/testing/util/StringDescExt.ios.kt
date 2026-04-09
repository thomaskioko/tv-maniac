package com.thomaskioko.tvmaniac.i18n.testing.util

import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import dev.icerock.moko.resources.desc.StringDesc

public actual fun StringResourceKey.getString(): String = this::class.simpleName.orEmpty()

public actual fun StringResourceKey.getString(vararg args: Any): String {
    return "${this::class.simpleName.orEmpty()}(${args.joinToString()})"
}

public actual fun PluralsResourceKey.getPlural(quantity: Int): String {
    return "${this::class.simpleName.orEmpty()}($quantity)"
}

public actual fun PluralsResourceKey.getPlural(quantity: Int, vararg args: Any): String {
    return "${this::class.simpleName.orEmpty()}($quantity, ${args.joinToString()})"
}

public actual suspend fun StringDesc.getString(): String = this.toString()
