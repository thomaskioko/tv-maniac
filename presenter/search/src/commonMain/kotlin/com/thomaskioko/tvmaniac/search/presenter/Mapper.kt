package com.thomaskioko.tvmaniac.search.presenter

import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.genre.model.GenreWithShowsEntity
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.search.presenter.model.CategoryItem
import com.thomaskioko.tvmaniac.search.presenter.model.GenreRowModel
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Inject
public class Mapper(
    private val formatterUtil: FormatterUtil,
    private val localizer: Localizer,
) {

    public fun toShowList(items: List<ShowEntity>): ImmutableList<ShowItem> =
        items.map {
            ShowItem(
                tmdbId = it.tmdbId,
                traktId = it.traktId,
                title = it.title,
                posterImageUrl = it.posterPath,
                inLibrary = it.inLibrary,
                status = it.status,
                voteAverage = it.voteAverage?.let { vote -> formatterUtil.formatDouble(vote, 1) },
                year = it.year,
                overview = it.overview,
            )
        }.toImmutableList()

    public fun toGenreRows(entities: List<GenreWithShowsEntity>): ImmutableList<GenreRowModel> =
        entities.map { entity ->
            GenreRowModel(
                slug = entity.genre.slug,
                name = entity.genre.name,
                subtitle = genreDescription(entity.genre.slug),
                shows = entity.shows.map { show ->
                    ShowItem(
                        tmdbId = show.tmdbId,
                        traktId = show.traktId,
                        title = show.title,
                        posterImageUrl = show.posterPath,
                        inLibrary = show.inLibrary,
                    )
                }.toImmutableList(),
            )
        }.toImmutableList()

    public fun toCategoryItems(): ImmutableList<CategoryItem> =
        GenreShowCategory.entries.map { category ->
            CategoryItem(
                category = category,
                label = categoryLabel(category),
            )
        }.toImmutableList()

    public fun categoryTitle(): String =
        localizer.getString(StringResourceKey.LabelGenreCategoryTitle)

    private fun categoryLabel(category: GenreShowCategory): String {
        val key = when (category) {
            GenreShowCategory.POPULAR -> StringResourceKey.LabelGenreCategoryPopular
            GenreShowCategory.TRENDING -> StringResourceKey.LabelGenreCategoryTrending
            GenreShowCategory.TOP_RATED -> StringResourceKey.LabelGenreCategoryTopRated
            GenreShowCategory.MOST_WATCHED -> StringResourceKey.LabelGenreCategoryMostWatched
        }
        return localizer.getString(key)
    }

    private fun genreDescription(slug: String): String {
        val key = when (slug) {
            "action" -> StringResourceKey.GenreDescAction
            "comedy" -> StringResourceKey.GenreDescComedy
            "drama" -> StringResourceKey.GenreDescDrama
            "fantasy" -> StringResourceKey.GenreDescFantasy
            "horror" -> StringResourceKey.GenreDescHorror
            "science-fiction" -> StringResourceKey.GenreDescScienceFiction
            "thriller" -> StringResourceKey.GenreDescThriller
            "animation" -> StringResourceKey.GenreDescAnimation
            "mystery" -> StringResourceKey.GenreDescMystery
            "romance" -> StringResourceKey.GenreDescRomance
            else -> return ""
        }
        return localizer.getString(key)
    }
}
