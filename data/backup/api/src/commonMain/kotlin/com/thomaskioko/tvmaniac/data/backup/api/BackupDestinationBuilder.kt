package com.thomaskioko.tvmaniac.data.backup.api

public interface BackupDestinationBuilder {
    public fun build(location: String): BackupDestination

    public fun safetyCopy(): BackupDestination
}
