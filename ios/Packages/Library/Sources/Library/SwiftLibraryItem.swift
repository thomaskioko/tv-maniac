import Components
import Foundation
import Models

public struct SwiftLibraryItem: Identifiable, Equatable {
    public var id: Int64 {
        showId
    }

    public let showId: Int64
    public let title: String
    public let posterUrl: String?
    public let year: String?
    public let status: String?
    public let seasonCount: Int64
    public let episodeCount: Int64
    public let rating: Double?
    public let genres: [String]?
    public let watchProviders: [SwiftProviders]

    public init(
        showId: Int64,
        title: String,
        posterUrl: String?,
        year: String?,
        status: String?,
        seasonCount: Int64,
        episodeCount: Int64,
        rating: Double?,
        genres: [String]?,
        watchProviders: [SwiftProviders]
    ) {
        self.showId = showId
        self.title = title
        self.posterUrl = posterUrl
        self.year = year
        self.status = status
        self.seasonCount = seasonCount
        self.episodeCount = episodeCount
        self.rating = rating
        self.genres = genres
        self.watchProviders = watchProviders
    }

    public var metadataComponents: [String] {
        var components: [String] = []
        if let year {
            components.append(year)
        }
        if let status {
            components.append(status)
        }
        let seasons = Int(seasonCount)
        if seasons > 0 {
            components.append(seasons == 1 ? "\(seasons) Season" : "\(seasons) Seasons")
        }
        let episodes = Int(episodeCount)
        if episodes > 0 {
            components.append(episodes == 1 ? "\(episodes) Episode" : "\(episodes) Episodes")
        }
        if let firstGenre = genres?.first {
            components.append(firstGenre)
        }
        return components
    }

    public var formattedRating: String? {
        guard let rating else { return nil }
        return rating.formatted(.number.precision(.fractionLength(1)))
    }
}
