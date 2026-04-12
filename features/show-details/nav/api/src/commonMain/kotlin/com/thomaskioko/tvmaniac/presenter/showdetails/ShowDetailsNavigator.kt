package com.thomaskioko.tvmaniac.presenter.showdetails

public interface ShowDetailsNavigator {
    public fun goBack()
    public fun showDetails(traktId: Long)
    public fun showSeasonDetails(param: ShowSeasonDetailsParam)
    public fun showTrailers(traktShowId: Long)
    public fun showFollowed()
}
