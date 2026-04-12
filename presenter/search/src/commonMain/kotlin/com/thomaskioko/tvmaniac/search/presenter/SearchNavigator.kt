package com.thomaskioko.tvmaniac.search.presenter

public interface SearchNavigator {
    public fun showDetails(traktId: Long)
    public fun showGenre(genreId: Long)
    public fun goBack()
}
