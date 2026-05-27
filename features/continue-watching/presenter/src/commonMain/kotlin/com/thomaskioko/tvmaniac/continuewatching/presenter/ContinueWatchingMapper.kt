package com.thomaskioko.tvmaniac.continuewatching.presenter

import com.thomaskioko.tvmaniac.continuewatching.presenter.model.ContinueWatchingItem
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.SectionedEpisodes
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.SectionedItems
import com.thomaskioko.tvmaniac.domain.continuewatching.model.UpNextSections
import com.thomaskioko.tvmaniac.domain.continuewatching.model.WatchlistSections
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Inject
public class ContinueWatchingMapper(
    private val localizer: Localizer,
) {
    public fun resolveLabels(query: String): ContinueWatchingLabels = ContinueWatchingLabels(
        watchingTitle = localizer.getString(StringResourceKey.TitleWatching),
        staleTitle = localizer.getString(StringResourceKey.TitleNotWatchedForWhile),
        upToDate = localizer.getString(StringResourceKey.LabelUpToDate),
        premiereBadge = localizer.getString(StringResourceKey.BadgePremiere),
        newBadge = localizer.getString(StringResourceKey.BadgeNew),
        emptyTitle = localizer.getString(
            if (query.isBlank()) {
                StringResourceKey.LabelWatchlistEmptyInProgress
            } else {
                StringResourceKey.GenericEmptyContent
            },
        ),
        emptyResultMessage = if (query.isNotBlank()) {
            localizer.getString(StringResourceKey.LabelWatchlistEmptyResult, query)
        } else {
            ""
        },
    )

    public fun toSectionedItems(
        sections: WatchlistSections,
        sortOption: WatchlistSortOption,
    ): SectionedItems {
        val sectioned = sections.toPresenter()
        return SectionedItems(
            watchNext = sectioned.watchNext.applySorting(sortOption),
            stale = sectioned.stale.applySorting(sortOption),
        )
    }

    public fun toSectionedEpisodes(sections: UpNextSections): SectionedEpisodes = sections.toPresenter()

    private fun ImmutableList<ContinueWatchingItem>.applySorting(
        sortOption: WatchlistSortOption,
    ): ImmutableList<ContinueWatchingItem> = when (sortOption) {
        WatchlistSortOption.ADDED_DESC -> sortedByDescending { it.lastWatchedAt ?: 0L }
        WatchlistSortOption.ADDED_ASC -> sortedBy { it.lastWatchedAt ?: Long.MAX_VALUE }
        WatchlistSortOption.RELEASED_DESC -> sortedByDescending { it.year.orEmpty() }
        WatchlistSortOption.RELEASED_ASC -> sortedBy { it.year.orEmpty() }
        WatchlistSortOption.TITLE_ASC -> sortedBy { it.title.lowercase() }
        WatchlistSortOption.TITLE_DESC -> sortedByDescending { it.title.lowercase() }
    }.toImmutableList()
}
