package com.thomaskioko.tvmaniac.common.voyagerutil

import com.thomaskioko.tvmaniac.presentation.discover.DiscoverScreenModel
import com.thomaskioko.tvmaniac.presentation.profile.ProfileScreenModel
import com.thomaskioko.tvmaniac.presentation.settings.SettingsScreenModel
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryScreenModel

interface PlatformScreenModelComponent {
    val discoverScreenModel: () -> DiscoverScreenModel
    val libraryScreenModel: () -> LibraryScreenModel
    val profileScreenModel: () -> ProfileScreenModel
    val settingsScreenModel: () -> SettingsScreenModel
}

expect interface ScreenModelComponent : PlatformScreenModelComponent
