package com.thomaskioko.tvmaniac.core.tasks.implementation

public sealed class TaskResult {
    public data object Success : TaskResult()
    public data class Failure(val message: String) : TaskResult()
}
