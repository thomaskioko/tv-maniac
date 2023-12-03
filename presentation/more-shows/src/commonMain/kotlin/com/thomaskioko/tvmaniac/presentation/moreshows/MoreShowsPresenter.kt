package com.thomaskioko.tvmaniac.presentation.moreshows

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.decompose.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias MoreShowsPresenterFactory = (
    ComponentContext,
    id: Long,
    onBack: () -> Unit,
    onNavigateToShowDetails: (id: Long) -> Unit,
) -> MoreShowsPresenter

@Inject
class MoreShowsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted categoryId: Long,
    @Assisted onBack: () -> Unit,
    @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val _state = MutableStateFlow(MoreShowsState())
    val state: Value<MoreShowsState> = _state
        .asValue(initialValue = _state.value, lifecycle = lifecycle)

    fun dispatch(action: MoreShowsActions) {
    }
}
