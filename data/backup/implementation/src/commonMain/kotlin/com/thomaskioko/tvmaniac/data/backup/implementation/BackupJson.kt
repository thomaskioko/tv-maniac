package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupFile
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

public object BackupJson {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
        explicitNulls = false
    }

    public fun encode(backup: BackupFile): String = json.encodeToString(backup)

    public fun decode(contents: String): BackupFile {
        val version = readVersion(contents)
        if (version > BackupFormat.VERSION) throw BackupVersionTooNewException(version)
        return json.decodeFromString(contents)
    }

    private fun readVersion(contents: String): Int {
        val element = json.parseToJsonElement(contents)
        val version = (element as? JsonObject)?.get("version")?.jsonPrimitive?.content?.toIntOrNull()
        return version ?: throw BackupVersionMissingException()
    }
}

public class BackupVersionTooNewException(public val version: Int) : Exception(
    "Backup version $version was written by a newer release of TvManiac",
)

public class BackupVersionMissingException : Exception("Backup file has no version")
