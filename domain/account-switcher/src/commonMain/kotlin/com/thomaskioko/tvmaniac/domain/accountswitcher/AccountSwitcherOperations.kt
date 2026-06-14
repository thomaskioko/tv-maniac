package com.thomaskioko.tvmaniac.domain.accountswitcher

public fun interface ResyncLibrary {
    public suspend operator fun invoke()
}

public fun interface ResyncContinueWatching {
    public suspend operator fun invoke()
}
