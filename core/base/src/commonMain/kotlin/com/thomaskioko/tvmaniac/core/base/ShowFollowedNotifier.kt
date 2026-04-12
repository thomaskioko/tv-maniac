package com.thomaskioko.tvmaniac.core.base

// TODO: Remove once the navigator refactor (presenter-navigation-refactor branch) merges.
//  ShowDetailsNavigator.showFollowed() replaces this interface. DefaultRootPresenter
//  handles the event in its when block like every other navigation event.
public interface ShowFollowedNotifier {
    public fun onShowFollowed()
}
