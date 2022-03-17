package com.thomaskioko.tvmaniac.settings

import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect
import com.thomaskioko.tvmaniac.shared.core.ui.State

data class SettingsState(
    val theme: Theme,
    val showPopup: Boolean
) : State {
    companion object {
        val DEFAULT = SettingsState(
            theme = Theme.SYSTEM,
            showPopup = false
        )
    }
}

sealed class SettingsActions : Action {
    data class ThemeSelected(
        val theme: String
    ) : SettingsActions()

    object LoadTheme : SettingsActions()
    object ThemeClicked : SettingsActions()
}

sealed class SettingsEffect : Effect

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}
