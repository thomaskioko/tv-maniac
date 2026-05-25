package com.thomaskioko.tvmaniac.myshows.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thomaskioko.tvmaniac.continuewatching.ui.ContinueWatchingScreen
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsPresenter
import io.github.thomaskioko.codegen.annotations.TabUi

@TabUi(presenter = MyShowsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun MyShowsScreen(
    presenter: MyShowsPresenter,
    modifier: Modifier = Modifier,
) {
    ContinueWatchingScreen(
        presenter = presenter.continueWatchingPresenter,
        modifier = modifier,
    )
}
