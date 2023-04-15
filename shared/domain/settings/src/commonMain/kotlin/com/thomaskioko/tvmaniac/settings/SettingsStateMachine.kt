package com.thomaskioko.tvmaniac.settings

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class SettingsStateMachine(
    private val datastoreRepository: DatastoreRepository
) : FlowReduxStateMachine<SettingsState, SettingsActions>(initialState = SettingsContent.EMPTY) {

    init {
        spec {

            inState<SettingsContent> {

                collectWhileInState(datastoreRepository.observeTheme()) { theme, state ->
                    state.mutate {
                        copy(theme = theme)
                    }
                }

                on<ChangeThemeClicked> { _, state ->
                    state.mutate {
                        copy(showPopup = true)
                    }
                }

                on<ThemeSelected> { action, state ->
                    datastoreRepository.saveTheme(action.theme)
                    state.mutate { copy(showPopup = false) }
                }

                on<DimissThemeClicked> { _, state ->
                    state.mutate {
                        copy(showPopup = false)
                    }
                }

                on<ShowTraktDialog> { _, state ->
                    state.mutate {
                        copy(showTraktDialog = true)
                    }
                }

                on<DismissTraktDialog> { _, state ->
                    state.mutate {
                        copy(showTraktDialog = false)
                    }
                }

                on<TraktLogin> { _, state ->
                    state.mutate {
                        copy(showTraktDialog = !showTraktDialog)
                    }
                }
            }
        }
    }
}