package com.thomaskioko.tvmaniac.navigation.root

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.root.model.EpisodeSheetConfig
import com.thomaskioko.tvmaniac.navigation.SheetChild

public interface EpisodeSheetChildFactory {
    public fun createChild(config: EpisodeSheetConfig, componentContext: ComponentContext): SheetChild
}
