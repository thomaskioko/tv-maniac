package com.thomaskioko.tvmaniac.settings

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SettingsStateMachine constructor(
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

/**
 * A wrapper class around [SettingsStateMachine] handling `Flow` and suspend functions on iOS.
 */
class SettingsStateMachineWrapper(
    private val stateMachine: SettingsStateMachine,
    dispatcher: MainCoroutineDispatcher,
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + dispatcher)

    fun start(stateChangeListener: (SettingsState) -> Unit) {
        scope.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: SettingsActions) {
        scope.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        job.cancelChildren()
    }
}