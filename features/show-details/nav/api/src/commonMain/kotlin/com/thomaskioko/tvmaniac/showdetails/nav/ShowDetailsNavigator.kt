package com.thomaskioko.tvmaniac.showdetails.nav

import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowSeasonDetailsParam

public interface ShowDetailsNavigator {
    public fun goBack()
    public fun showDetails(traktId: Long)
    public fun showSeasonDetails(param: ShowSeasonDetailsParam)
    public fun showTrailers(traktShowId: Long)
    public fun showFollowed()
}
