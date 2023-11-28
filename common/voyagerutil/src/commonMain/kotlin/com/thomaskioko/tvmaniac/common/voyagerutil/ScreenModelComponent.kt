package com.thomaskioko.tvmaniac.common.voyagerutil

import com.thomaskioko.tvmaniac.presentation.discover.DiscoverScreenModel
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryScreenModel

interface PlatformScreenModelComponent {
    val discoverScreenModel: () -> DiscoverScreenModel
    val libraryScreenModel: () -> LibraryScreenModel
}

expect interface ScreenModelComponent : PlatformScreenModelComponent
