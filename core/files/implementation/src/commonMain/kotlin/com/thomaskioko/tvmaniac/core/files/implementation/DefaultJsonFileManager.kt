package com.thomaskioko.tvmaniac.core.files.implementation

import com.thomaskioko.tvmaniac.core.files.api.FileManager
import com.thomaskioko.tvmaniac.core.files.api.JsonFileManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@ContributesBinding(AppScope::class)
public class DefaultJsonFileManager(
    private val fileManager: FileManager,
) : JsonFileManager {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> writeToFile(directoryPath: String, fileName: String, value: T, type: KClass<T>) {
        fileManager.writeToFile(
            directoryPath = directoryPath,
            fileName = fileName,
            contents = json.encodeToString(type.serializer(), value),
        )
    }

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> getFileContent(directoryPath: String, fileName: String, type: KClass<T>): T? =
        fileManager.getFileContent(directoryPath, fileName)
            ?.let { json.decodeFromString(type.serializer(), it) }
}
