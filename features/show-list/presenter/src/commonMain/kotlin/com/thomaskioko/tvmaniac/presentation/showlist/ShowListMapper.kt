package com.thomaskioko.tvmaniac.presentation.showlist

import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.presentation.showlist.model.TraktListModel
import com.thomaskioko.tvmaniac.traktlists.api.TraktList
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Inject
public class ShowListMapper(
    private val localizer: Localizer,
) {
    public fun toModels(lists: List<TraktList>, togglingListIds: Set<Long>): ImmutableList<TraktListModel> =
        lists.map { list ->
            TraktListModel(
                id = list.id,
                slug = list.slug,
                name = list.name,
                description = list.description,
                showCountText = localizer.getPlural(
                    key = PluralsResourceKey.ShowCount,
                    quantity = list.itemCount.toInt(),
                    list.itemCount.toInt(),
                ),
                isShowInList = list.isShowInList,
                isToggling = list.id in togglingListIds,
            )
        }.toImmutableList()

    public fun resolveCopy(): ShowListCopy = ShowListCopy(
        sheetTitle = localizer.getString(StringResourceKey.LabelWatchlistSaveToList),
        createListButtonText = localizer.getString(StringResourceKey.LabelWatchlistCreateCustomList),
        createListDoneText = localizer.getString(StringResourceKey.LabelWatchlistDone),
        createListPlaceholder = localizer.getString(StringResourceKey.LabelWatchlistNewListPlaceholder),
        emptyListText = localizer.getString(StringResourceKey.LabelWatchlistEmptyList),
        listsHeaderText = localizer.getString(StringResourceKey.LabelWatchlistYourLists),
        loginRequiredTitle = localizer.getString(StringResourceKey.LabelWatchlistLoginRequiredTitle),
        loginRequiredMessage = localizer.getString(StringResourceKey.LabelWatchlistLoginRequiredMessage),
        loginRequiredConfirmText = localizer.getString(StringResourceKey.LabelOk),
    )
}
