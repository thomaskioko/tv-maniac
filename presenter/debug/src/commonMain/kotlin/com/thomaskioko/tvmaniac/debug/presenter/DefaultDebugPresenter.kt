package com.thomaskioko.tvmaniac.debug.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, DebugPresenter::class)
public class DefaultDebugPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val backClicked: () -> Unit,
    private val logger: Logger,
) : DebugPresenter, ComponentContext by componentContext {

    private val _state = MutableStateFlow(DebugState.DEFAULT_STATE)
    override val state: StateFlow<DebugState> = _state.asStateFlow()

    override fun dispatch(action: DebugActions) {
        when (action) {
            BackClicked -> backClicked()
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, DebugPresenter.Factory::class)
public class DefaultDebugPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        backClicked: () -> Unit,
    ) -> DebugPresenter,
) : DebugPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        backClicked: () -> Unit,
    ): DebugPresenter = presenter(componentContext, backClicked)
}
