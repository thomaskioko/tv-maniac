package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

internal fun loadJson(fileName: String): String =
    Thread.currentThread().contextClassLoader!!.getResource(fileName)!!.readText()
