package com.thomaskioko.tvmaniac.presenter.showdetails

import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.presenter.showdetails.model.CastModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ContinueTrackingEpisodeModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.SeasonModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.TrailerModel
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.thomaskioko.tvmaniac.domain.showdetails.model.Casts as DomainCasts
import com.thomaskioko.tvmaniac.domain.showdetails.model.Providers as DomainProviders
import com.thomaskioko.tvmaniac.domain.showdetails.model.Season as DomainSeason
import com.thomaskioko.tvmaniac.domain.showdetails.model.Show as DomainShow
import com.thomaskioko.tvmaniac.domain.showdetails.model.ShowDetails as DomainShowDetails
import com.thomaskioko.tvmaniac.domain.showdetails.model.ShowMetadata as DomainShowMetadata
import com.thomaskioko.tvmaniac.domain.showdetails.model.Trailer as DomainTrailer

@Inject
public class ShowDetailsMapper(
    private val localizer: Localizer,
) {

    public fun applyShowDetails(
        current: ShowDetailsModel,
        details: DomainShowDetails,
    ): ShowDetailsModel = current.copy(
        tmdbId = details.tmdbId,
        title = details.title,
        overview = details.overview,
        language = details.language,
        posterImageUrl = details.posterImageUrl,
        backdropImageUrl = details.backdropImageUrl,
        votes = details.votes,
        rating = details.rating,
        year = details.year,
        status = details.status?.localizeStatus(),
        isInLibrary = details.isInLibrary,
        genres = details.genres.toImmutableList(),
    )

    public fun applyMetadata(
        current: ShowDetailsModel,
        metadata: DomainShowMetadata,
    ): ShowDetailsModel = current.copy(
        hasWebViewInstalled = metadata.hasWebViewInstalled,
        numberOfSeasons = metadata.seasonsList.size,
        providers = metadata.providers.toWatchProviderList(),
        castsList = metadata.castsList.toCastList(),
        seasonsList = metadata.seasonsList.toSeasonsList(),
        similarShows = metadata.similarShows.toShowList(),
        trailersList = metadata.trailersList.toTrailerList(),
    )

    public fun applyWatchProgress(
        current: ShowDetailsModel,
        progress: ShowWatchProgress,
    ): ShowDetailsModel = current.copy(
        watchedEpisodesCount = progress.watchedCount,
        totalEpisodesCount = progress.totalCount,
        watchProgress = progress.progressPercentage,
    )

    public fun mapContinueTrackingEpisodes(
        episodes: ImmutableList<EpisodeDetails>,
        showTraktId: Long,
    ): ImmutableList<ContinueTrackingEpisodeModel> =
        episodes.map { it.toContinueTrackingModel(showTraktId) }.toImmutableList()

    private fun String.localizeStatus(): String {
        val key = when (lowercase()) {
            "returning series" -> StringResourceKey.LabelLibraryStatusReturning
            "planned" -> StringResourceKey.LabelLibraryStatusPlanned
            "in production" -> StringResourceKey.LabelLibraryStatusInProduction
            "ended" -> StringResourceKey.LabelLibraryStatusEnded
            "canceled", "cancelled" -> StringResourceKey.LabelLibraryStatusCanceled
            else -> return capitalizeFirstCharacter()
        }
        return localizer.getString(key)
    }

    private fun List<DomainCasts>.toCastList(): ImmutableList<CastModel> =
        map {
            CastModel(
                id = it.id,
                name = it.name,
                profileUrl = it.profileUrl,
                characterName = it.characterName,
            )
        }.toImmutableList()

    private fun List<DomainShow>.toShowList(): ImmutableList<ShowModel> =
        map {
            ShowModel(
                traktId = it.traktId,
                title = it.title,
                posterImageUrl = it.posterImageUrl,
                backdropImageUrl = it.backdropImageUrl,
                isInLibrary = it.isInLibrary,
            )
        }.toImmutableList()

    private fun List<DomainProviders>.toWatchProviderList(): ImmutableList<ProviderModel> =
        map {
            ProviderModel(
                id = it.id,
                name = it.name,
                logoUrl = it.logoUrl,
            )
        }.toImmutableList()

    private fun List<DomainSeason>.toSeasonsList(): ImmutableList<SeasonModel> =
        map {
            SeasonModel(
                seasonId = it.seasonId,
                tvShowId = it.tvShowId,
                name = it.name,
                seasonNumber = it.seasonNumber,
                watchedCount = it.watchedCount,
                totalCount = it.totalCount,
            )
        }.toImmutableList()

    private fun List<DomainTrailer>.toTrailerList(): ImmutableList<TrailerModel> =
        map {
            TrailerModel(
                showTmdbId = it.showTmdbId,
                key = it.key,
                name = it.name,
                youtubeThumbnailUrl = it.youtubeThumbnailUrl,
            )
        }.toImmutableList()

    private fun EpisodeDetails.toContinueTrackingModel(showTraktId: Long): ContinueTrackingEpisodeModel {
        val seasonStr = "S${seasonNumber.toString().padStart(2, '0')}"
        val episodeStr = "E${episodeNumber.toString().padStart(2, '0')}"
        return ContinueTrackingEpisodeModel(
            episodeId = id,
            seasonId = seasonId,
            showTraktId = showTraktId,
            episodeNumber = episodeNumber,
            seasonNumber = seasonNumber,
            episodeNumberFormatted = "$seasonStr | $episodeStr",
            episodeTitle = name,
            imageUrl = stillPath,
            isWatched = isWatched,
            daysUntilAir = daysUntilAir,
            hasAired = hasAired,
        )
    }

    private fun String.capitalizeFirstCharacter(): String = replaceFirstChar { char -> char.uppercase() }
}
