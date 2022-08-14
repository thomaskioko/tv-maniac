package com.thomaskioko.tvmaniac.shared.persistance

import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect

sealed class SettingsState

data class SettingsContent(
    val theme: Theme,
    val showPopup: Boolean
) : SettingsState() {
    companion object {
        val DEFAULT = SettingsContent(
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
