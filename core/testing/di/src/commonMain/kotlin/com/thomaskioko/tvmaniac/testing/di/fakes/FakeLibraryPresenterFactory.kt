package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presentation.library.LibraryAction
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.library.LibraryState
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, LibraryPresenter.Factory::class)
public class FakeLibraryPresenterFactory : LibraryPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ): LibraryPresenter = FakeLibraryPresenter()
}

internal class FakeLibraryPresenter : LibraryPresenter {
    override val state: StateFlow<LibraryState> = MutableStateFlow(LibraryState())

    override fun dispatch(action: LibraryAction) {
    }
}
