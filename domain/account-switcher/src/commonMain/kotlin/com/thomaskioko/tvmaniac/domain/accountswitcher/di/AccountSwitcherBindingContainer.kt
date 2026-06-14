package com.thomaskioko.tvmaniac.domain.accountswitcher.di

import com.thomaskioko.tvmaniac.domain.accountswitcher.ResyncContinueWatching
import com.thomaskioko.tvmaniac.domain.accountswitcher.ResyncLibrary
import com.thomaskioko.tvmaniac.domain.accountswitcher.ResyncProfile
import com.thomaskioko.tvmaniac.domain.continuewatching.SyncContinueWatchingInteractor
import com.thomaskioko.tvmaniac.domain.library.SyncLibraryInteractor
import com.thomaskioko.tvmaniac.domain.user.UpdateUserProfileData
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object AccountSwitcherBindingContainer {

    @Provides
    public fun provideResyncLibrary(
        syncLibraryInteractor: SyncLibraryInteractor,
    ): ResyncLibrary = ResyncLibrary {
        syncLibraryInteractor.executeSync(SyncLibraryInteractor.Param(forceRefresh = true))
    }

    @Provides
    public fun provideResyncContinueWatching(
        syncContinueWatchingInteractor: SyncContinueWatchingInteractor,
    ): ResyncContinueWatching = ResyncContinueWatching {
        syncContinueWatchingInteractor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = true))
    }

    @Provides
    public fun provideResyncProfile(
        updateUserProfileData: UpdateUserProfileData,
    ): ResyncProfile = ResyncProfile {
        updateUserProfileData.executeSync(UpdateUserProfileData.Params(forceRefresh = true))
    }
}
