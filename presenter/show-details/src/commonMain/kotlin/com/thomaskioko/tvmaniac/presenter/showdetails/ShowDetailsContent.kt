package com.thomaskioko.tvmaniac.presenter.showdetails

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ContinueTrackingEpisodeModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsModel
import com.thomaskioko.tvmaniac.traktlists.api.TraktListWithMembership
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class ShowDetailsContent(
    val showDetailsRefreshing: Boolean = false,
    val similarShowsRefreshing: Boolean = false,
    val watchProvidersRefreshing: Boolean = false,
    val showListSheet: Boolean = false,
    val showLoginPrompt: Boolean = false,
    val showDetails: ShowDetailsModel = ShowDetailsModel.Empty,
    val selectedSeasonIndex: Int = -1,
    val continueTrackingEpisodes: ImmutableList<ContinueTrackingEpisodeModel> = persistentListOf(),
    val continueTrackingScrollIndex: Int = 0,
    val traktLists: ImmutableList<TraktListWithMembership> = persistentListOf(),
    val showCreateListField: Boolean = false,
    val isCreatingList: Boolean = false,
    val createListName: String = "",
    val createListError: String? = null,
    val sheetTitle: String = "",
    val createListButtonText: String = "",
    val createListDoneText: String = "",
    val createListPlaceholder: String = "",
    val emptyListText: String = "",
    val listsHeaderText: String = "",
    val loginRequiredTitle: String = "",
    val loginRequiredMessage: String = "",
    val loginRequiredConfirmText: String = "",
    val message: UiMessage? = null,
) {
    public val isRefreshing: Boolean
        get() = showDetailsRefreshing || similarShowsRefreshing || watchProvidersRefreshing

    public companion object {
        public val Empty: ShowDetailsContent = ShowDetailsContent(showDetailsRefreshing = true)
    }
}
