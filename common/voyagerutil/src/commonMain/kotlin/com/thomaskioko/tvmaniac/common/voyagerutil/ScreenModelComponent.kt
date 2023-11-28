package com.thomaskioko.tvmaniac.common.voyagerutil

import com.thomaskioko.tvmaniac.presentation.discover.DiscoverScreenModel

interface PlatformScreenModelComponent {
    val discoverScreenModel: () -> DiscoverScreenModel
}

expect interface ScreenModelComponent : PlatformScreenModelComponent
