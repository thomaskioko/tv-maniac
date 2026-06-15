import TvManiac

// MARK: - Calendar Mapping

public extension TvManiac.CalendarDateGroup {
    func toSwift() -> SwiftCalendarDateGroup {
        .init(
            dateLabel: dateLabel,
            episodes: episodes.compactMap { $0.toSwift() }
        )
    }
}

public extension TvManiac.CalendarEpisodeItem {
    func toSwift() -> SwiftCalendarEpisodeItem {
        .init(
            showId: showId,
            episodeId: episodeId as! Int64,
            showTitle: showTitle,
            posterUrl: posterUrl,
            episodeInfo: episodeInfo,
            airTime: airTime,
            network: network,
            additionalEpisodesCount: additionalEpisodesCount,
            overview: overview,
            rating: rating?.doubleValue,
            votes: votes?.int32Value,
            runtime: runtime?.int32Value,
            formattedAirDate: formattedAirDate
        )
    }
}
