import SwiftUIComponents
import TvManiac

// MARK: - ImageQuality Mapping

public extension TvManiac.ImageQuality {
    func toSwift() -> SwiftImageQuality {
        switch self {
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
            tmdbId: tmdbId,
            title: title,
            posterUrl: posterImageUrl,
            inLibrary: true
        )
    }
}

public extension TvManiac.SeasonImagesModel {
    func toSwift() -> ShowPosterImage {
        .init(
            tmdbId: id,
            title: "",
            posterUrl: imageUrl,
            inLibrary: false
        )
    }
}

public extension TvManiac.TvShow {
    func toSwift() -> ShowPosterImage {
        .init(
            tmdbId: tmdbId,
            title: title,
            posterUrl: posterImageUrl,
            inLibrary: inLibrary
        )
    }
}

public extension TvManiac.DiscoverShow {
    func toSwift() -> SwiftShow {
        .init(
            tmdbId: tmdbId,
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
        .init(tmdbId: id, name: name, imageUrl: posterUrl)
    }
}

public extension TvManiac.ShowModel {
    func toSwift() -> SwiftShow {
        .init(
            tmdbId: tmdbId,
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
            showId: showId,
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
            name: name
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
            tmdbId: tmdbId,
            title: title,
            posterUrl: posterImageUrl,
            inLibrary: inLibrary
        )
    }
}

public extension TvManiac.EpisodeDetailsModel {
    func toSwift() -> SwiftEpisode {
        .init(episodeId: id, title: episodeTitle, overview: overview, imageUrl: imageUrl)
    }
}

public extension TvManiac.SeasonImagesModel {
    func toSwift() -> SwiftShow {
        .init(tmdbId: id, title: "", posterUrl: imageUrl, inLibrary: false)
    }
}

public extension TvManiac.ShowItem {
    func toSwift() -> SwiftShow {
        .init(
            tmdbId: tmdbId,
            title: title,
            posterUrl: posterImageUrl,
            backdropUrl: nil,
            inLibrary: inLibrary
        )
    }

    func toSwift() -> SwiftSearchShow {
        .init(
            tmdbId: tmdbId,
            title: title,
            overview: overview,
            status: status,
            imageUrl: posterImageUrl,
            year: year,
            voteAverage: voteAverage?.doubleValue
        )
    }
}
