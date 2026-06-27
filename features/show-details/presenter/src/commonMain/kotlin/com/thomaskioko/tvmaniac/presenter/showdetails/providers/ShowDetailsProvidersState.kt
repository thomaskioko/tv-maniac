package com.thomaskioko.tvmaniac.presenter.showdetails.providers

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class ShowDetailsProvidersState(
    val providers: ImmutableList<ProviderModel> = persistentListOf(),
    val isRefreshing: Boolean = false,
    val message: UiMessage? = null,
)
