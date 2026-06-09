package com.thomaskioko.tvmaniac.profile.presenter.model

import com.thomaskioko.tvmaniac.core.view.UiMessage
import kotlinx.collections.immutable.ImmutableList

public sealed interface SectionState<out T> {
    public data object Loading : SectionState<Nothing>

    public data object Empty : SectionState<Nothing>

    public data class Error(public val message: UiMessage) : SectionState<Nothing>

    public data class Content<out T>(public val items: ImmutableList<T>) : SectionState<T>
}
