package com.thomaskioko.tvmaniac.presentation.moreshows

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    dispatchersProvider: AppCoroutineDispatchers,
    @Assisted componentContext: ComponentContext,
    @Assisted categoryId: Long,
    @Assisted onBack: () -> Unit,
    @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
) {

    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatchersProvider.main)

    private val _state = MutableStateFlow(MoreShowsState())
    val state: StateFlow<MoreShowsState> = _state.asStateFlow()

    fun dispatch(action: MoreShowsActions) {
    }
}
