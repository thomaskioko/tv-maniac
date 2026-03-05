import Foundation

public struct SwiftCalendarDateGroup: Identifiable, Equatable {
    public let id: String
    public let dateLabel: String
    public let episodes: [SwiftCalendarEpisodeItem]

    public init(
        dateLabel: String,
        episodes: [SwiftCalendarEpisodeItem]
    ) {
        id = dateLabel
        self.dateLabel = dateLabel
        self.episodes = episodes
    }
}

public struct SwiftCalendarEpisodeItem: Identifiable, Equatable {
    public let id: String
    public let showTraktId: Int64
    public let episodeTraktId: Int64
    public let showTitle: String
    public let posterUrl: String?
    public let episodeInfo: String
    public let airTime: String?
    public let network: String?
    public let additionalEpisodesCount: Int32
    public let overview: String?
    public let rating: Double?
    public let votes: Int32?
    public let runtime: Int32?
    public let formattedAirDate: String?

    public init(
        showTraktId: Int64,
        episodeTraktId: Int64,
        showTitle: String,
        posterUrl: String?,
        episodeInfo: String,
        airTime: String?,
        network: String?,
        additionalEpisodesCount: Int32,
        overview: String? = nil,
        rating: Double? = nil,
        votes: Int32? = nil,
        runtime: Int32? = nil,
        formattedAirDate: String? = nil
    ) {
        id = "\(showTraktId)_\(episodeTraktId)"
        self.showTraktId = showTraktId
        self.episodeTraktId = episodeTraktId
        self.showTitle = showTitle
        self.posterUrl = posterUrl
        self.episodeInfo = episodeInfo
        self.airTime = airTime
        self.network = network
        self.additionalEpisodesCount = additionalEpisodesCount
        self.overview = overview
        self.rating = rating
        self.votes = votes
        self.runtime = runtime
        self.formattedAirDate = formattedAirDate
    }
}
