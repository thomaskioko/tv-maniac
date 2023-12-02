package com.thomaskioko.tvmaniac.presentation.search

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias SearchPresenterFactory = (
    ComponentContext,
    goBack: () -> Unit,
) -> SearchPresenter

@Inject
class SearchPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted goBack: () -> Unit,
) : ComponentContext by componentContext
