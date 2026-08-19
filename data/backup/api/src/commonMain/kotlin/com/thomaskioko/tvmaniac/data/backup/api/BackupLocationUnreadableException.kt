package com.thomaskioko.tvmaniac.data.backup.api

public class BackupLocationUnreadableException(location: String) : Exception("Cannot open $location")
