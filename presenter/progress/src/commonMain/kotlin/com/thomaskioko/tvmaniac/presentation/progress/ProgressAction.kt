package com.thomaskioko.tvmaniac.presentation.progress

public sealed interface ProgressAction {
    public data class SelectPage(val index: Int) : ProgressAction
}
