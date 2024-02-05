package com.thomaskioko.tvmaniac.presentation.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.decompose.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias SearchPresenterFactory =
  (
    ComponentContext,
    goBack: () -> Unit,
  ) -> SearchPresenter

@Inject
class SearchPresenter(
  @Assisted componentContext: ComponentContext,
  @Assisted goBack: () -> Unit,
) : ComponentContext by componentContext {

  private val coroutineScope = coroutineScope()
  private val _state: MutableStateFlow<SearchState> = MutableStateFlow(SearchLoading)

  val state: Value<SearchState> = _state.asValue(initialValue = _state.value, lifecycle = lifecycle)
}
