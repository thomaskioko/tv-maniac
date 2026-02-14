import SwiftUIComponents
import TvManiac

// MARK: - ImageQuality Mapping

public extension TvManiac.ImageQuality {
    func toSwift() -> SwiftImageQuality {
        switch self {
        case .auto:
            .auto
        case .high:
            .high
        case .medium:
            .medium
        case .low:
            .low
        }
    }

    static func fromSwift(_ swiftQuality: SwiftImageQuality) -> TvManiac.ImageQuality {
        switch swiftQuality {
        case .auto:
            .auto
        case .high:
            .high
        case .medium:
            .medium
        case .low:
            .low
        }
    }
}

public extension TvManiac.WatchlistItem {
    func toSwift() -> ShowPosterImage {
        .init(
            traktId: traktId,
            title: title,
            posterUrl: posterImageUrl,
            inLibrary: true
        )
    }
}

public extension TvManiac.SeasonImagesModel {
    func toSwift() -> ShowPosterImage {
        .init(
            traktId: id,
            title: "",
            posterUrl: imageUrl,
            inLibrary: false
        )
    }
}

public extension TvManiac.TvShow {
    func toSwift() -> ShowPosterImage {
        .init(
            traktId: traktId,
            title: title,
            posterUrl: posterImageUrl,
            inLibrary: inLibrary
        )
    }
}

public extension TvManiac.DiscoverShow {
    func toSwift() -> SwiftShow {
        .init(
            traktId: traktId,
            title: title,
            posterUrl: posterImageUrl,
            backdropUrl: nil,
            inLibrary: inLibrary,
            overview: overView
        )
    }
}

public extension TvManiac.ShowGenre {
    func toSwift() -> SwiftShowGenre {
        .init(traktId: id, tmdbId: id, name: name, imageUrl: posterUrl)
    }
}

public extension TvManiac.ShowModel {
    func toSwift() -> SwiftShow {
        .init(
            traktId: traktId,
            title: title,
            posterUrl: posterImageUrl,
            backdropUrl: nil,
            inLibrary: isInLibrary
        )
    }
}

public extension TvManiac.TrailerModel {
    func toSwift() -> SwiftTrailer {
        .init(
            showTmdbId: showTmdbId,
            key: key,
            name: name,
            youtubeThumbnailUrl: youtubeThumbnailUrl
        )
    }
}

public extension TvManiac.CastModel {
    func toSwift() -> SwiftCast {
        .init(
            castId: id,
            name: name,
            characterName: characterName,
            profileUrl: profileUrl
        )
    }
}

public extension TvManiac.ProviderModel {
    func toSwift() -> SwiftProviders {
        .init(providerId: id, logoUrl: logoUrl)
    }
}

public extension TvManiac.SeasonModel {
    func toSwift() -> SwiftSeason {
        .init(
            tvShowId: tvShowId,
            seasonId: seasonId,
            seasonNumber: seasonNumber,
            name: name,
            watchedCount: watchedCount,
            totalCount: totalCount,
            progressPercentage: progressPercentage
        )
    }
}

public extension String {
    func toSwift() -> SwiftGenres {
        .init(name: self)
    }
}

public extension TvManiac.TvShow {
    func toSwift() -> SwiftShow {
        .init(
            traktId: traktId,
            title: title,
            posterUrl: posterImageUrl,
            inLibrary: inLibrary
        )
    }
}

public extension TvManiac.EpisodeDetailsModel {
    func toSwift() -> SwiftEpisode {
        .init(
            episodeId: id,
            title: episodeNumberTitle,
            overview: overview,
            imageUrl: imageUrl,
            seasonNumber: seasonNumber,
            episodeNumber: Int64(episodeNumber),
            isWatched: isWatched,
            isEpisodeUpdating: isEpisodeUpdating,
            daysUntilAir: daysUntilAir?.int64Value,
            hasPreviousUnwatched: hasPreviousUnwatched,
            hasAired: hasAired
        )
    }
}

public extension TvManiac.SeasonImagesModel {
    func toSwift() -> SwiftShow {
        .init(
            traktId: id,
            title: "",
            posterUrl: imageUrl,
            inLibrary: false
        )
    }
}

public extension TvManiac.NextEpisodeUiModel {
    func toSwift() -> SwiftNextEpisode {
        .init(
            showTraktId: showTraktId,
            showName: showName,
            imageUrl: imageUrl,
            episodeId: episodeId,
            episodeTitle: episodeTitle,
            episodeNumber: episodeNumberFormatted,
            seasonId: seasonId,
            seasonNumber: seasonNumber,
            episodeNumberValue: episodeNumber,
            runtime: runtime,
            overview: overview,
            badge: isNew ? .new : .none
        )
    }
}

public extension TvManiac.ShowItem {
    func toSwift() -> SwiftShow {
        .init(
            traktId: traktId,
            title: title,
            posterUrl: posterImageUrl,
            backdropUrl: nil,
            inLibrary: inLibrary
        )
    }

    func toSwift() -> SwiftSearchShow {
        .init(
            tmdbId: tmdbId,
            traktId: traktId,
            title: title,
            overview: overview,
            status: status,
            imageUrl: posterImageUrl,
            year: year,
            voteAverage: voteAverage?.doubleValue
        )
    }
}

public extension TvManiac.ContinueTrackingEpisodeModel {
    func toSwift() -> SwiftContinueTrackingEpisode {
        .init(
            episodeId: episodeId,
            seasonId: seasonId,
            showTraktId: showTraktId,
            episodeNumber: episodeNumber,
            seasonNumber: seasonNumber,
            episodeNumberFormatted: episodeNumberFormatted,
            episodeTitle: episodeTitle,
            imageUrl: imageUrl,
            isWatched: isWatched,
            daysUntilAir: daysUntilAir?.int64Value,
            hasAired: hasAired
        )
    }
}

public extension TvManiac.UpNextEpisodeItem {
    func toSwift() -> SwiftNextEpisode {
        .init(
            showTraktId: showTraktId,
            showName: showName,
            imageUrl: showPoster,
            episodeId: episodeId,
            episodeTitle: episodeTitle,
            episodeNumber: episodeNumberFormatted,
            seasonId: seasonId,
            seasonNumber: seasonNumber,
            episodeNumberValue: episodeNumber,
            runtime: runtime,
            overview: overview,
            badge: badge?.toSwift() ?? .none,
            remainingEpisodes: remainingEpisodes
        )
    }
}

public extension TvManiac.EpisodeBadge {
    func toSwift() -> SwiftEpisodeBadge {
        switch self {
        case .premiere:
            .premiere
        case .theNew:
            .new
        }
    }
}

public extension TvManiac.UpNextEpisodeUiModel {
    func toSwift() -> SwiftNextEpisode {
        .init(
            showTraktId: showTraktId,
            showName: showName,
            imageUrl: imageUrl,
            episodeId: episodeId?.int64Value ?? 0,
            episodeTitle: episodeName ?? "",
            episodeNumber: formattedEpisodeNumber,
            seasonId: seasonId?.int64Value ?? 0,
            seasonNumber: seasonNumber?.int64Value ?? 0,
            episodeNumberValue: episodeNumber?.int64Value ?? 0,
            runtime: formattedRuntime,
            overview: overview ?? "",
            remainingEpisodes: Int32(remainingEpisodes),
            watchedCount: watchedCount,
            totalCount: totalCount
        )
    }
}

public extension TvManiac.DiscoverViewState {
    var featuredShowsSwift: [SwiftShow] {
        featuredShows.map { $0.toSwift() }
    }

    var nextEpisodesSwift: [SwiftNextEpisode] {
        nextEpisodes.map { $0.toSwift() }
    }

    var trendingTodaySwift: [SwiftShow] {
        trendingToday.map { $0.toSwift() }
    }

    var upcomingShowsSwift: [SwiftShow] {
        upcomingShows.map { $0.toSwift() }
    }

    var popularShowsSwift: [SwiftShow] {
        popularShows.map { $0.toSwift() }
    }

    var topRatedShowsSwift: [SwiftShow] {
        topRatedShows.map { $0.toSwift() }
    }
}

public extension TvManiac.LibraryShowItem {
    func toSwift() -> SwiftLibraryItem {
        .init(
            traktId: traktId,
            title: title,
            posterUrl: posterImageUrl,
            year: year,
            status: status,
            seasonCount: seasonCount,
            episodeCount: episodeCount,
            rating: rating?.doubleValue,
            genres: genres?.map { String($0) },
            watchProviders: watchProviders.map { $0.toSwift() }
        )
    }
}

public extension TvManiac.WatchProviderUiModel {
    func toSwift() -> SwiftProviders {
        .init(providerId: id, logoUrl: logoUrl)
    }
}
