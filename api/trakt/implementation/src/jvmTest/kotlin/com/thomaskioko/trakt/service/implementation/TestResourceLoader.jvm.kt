package com.thomaskioko.trakt.service.implementation

internal fun loadJson(fileName: String): String =
    Thread.currentThread().contextClassLoader!!.getResource(fileName)!!.readText()
