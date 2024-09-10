import SwiftUI
import TvManiac
import TvManiacUI

extension TvManiac.Show {
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

extension TvManiac.Trailer {
    func toSwift() -> SwiftTrailer {
        .init(
            showId: showId,
            key: key,
            name: name,
            youtubeThumbnailUrl: youtubeThumbnailUrl
        )
    }
}

extension TvManiac.Casts {
    func toSwift() -> SwiftCast {
        .init(
            castId: id,
            name: name,
            characterName: characterName,
            profileUrl: profileUrl
        )
    }
}

extension TvManiac.Providers {
    func toSwift() -> SwiftProviders {
        .init(providerId: id, logoUrl: logoUrl)
    }
}

extension Season {
    func toSwift() -> SwiftSeason {
        .init(
            tvShowId: tvShowId,
            seasonId: seasonId,
            seasonNumber: seasonNumber,
            name: name
        )
    }
}

extension String {
    func toSwift() -> SwiftGenres {
        .init(name: self)
    }
}

extension TvManiac.TvShow {
    func toSwift() -> SwiftShow {
        .init(
            tmdbId: tmdbId,
            title: title,
            posterUrl: posterImageUrl,
            inLibrary: inLibrary
        )
    }
}

extension EpisodeDetailsModel {
    func toSwift() -> SwiftEpisode {
        .init(episodeId: id, title: episodeTitle, overview: overview, imageUrl: imageUrl)
    }
}

extension SeasonImagesModel {
    func toSwift() -> SwiftShow {
        .init(tmdbId: id, title: "", posterUrl: imageUrl, inLibrary: false)
    }
}
