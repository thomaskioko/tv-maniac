package com.thomaskioko.tvmaniac.core.files.api

import kotlin.reflect.KClass

public interface JsonFileManager {

    public fun <T : Any> writeToFile(
        directoryPath: String,
        fileName: String,
        value: T,
        type: KClass<T>,
    )

    public fun <T : Any> getFileContent(
        directoryPath: String,
        fileName: String,
        type: KClass<T>,
    ): T?
}
