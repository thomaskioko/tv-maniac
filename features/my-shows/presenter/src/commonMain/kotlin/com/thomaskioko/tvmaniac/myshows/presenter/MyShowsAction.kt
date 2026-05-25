package com.thomaskioko.tvmaniac.myshows.presenter

public sealed interface MyShowsAction {
    public data class SelectPage(val index: Int) : MyShowsAction
}
