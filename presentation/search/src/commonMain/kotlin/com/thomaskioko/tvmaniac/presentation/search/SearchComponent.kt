package com.thomaskioko.tvmaniac.presentation.search

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias SearchComponentFactory =
  (
    ComponentContext,
    goBack: () -> Unit,
  ) -> SearchComponent

@Inject
class SearchComponent(
  @Assisted componentContext: ComponentContext,
  @Assisted goBack: () -> Unit,
) : ComponentContext by componentContext {

  private val _state: MutableStateFlow<SearchState> = MutableStateFlow(SearchLoading)
  val state: StateFlow<SearchState> = _state.asStateFlow()
}
