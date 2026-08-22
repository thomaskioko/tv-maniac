package com.thomaskioko.tvmaniac.domain.backup

public sealed interface AutoBackupResult {
    public data class Success(val showCount: Int) : AutoBackupResult

    public data object NoLocation : AutoBackupResult

    public data object LocationLost : AutoBackupResult

    public data class Failed(val message: String?) : AutoBackupResult
}
