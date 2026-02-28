import Foundation

public struct SwiftSeason: Identifiable, Equatable {
    public var id: Int64 {
        seasonId
    }

    public let tvShowId: Int64
    public let seasonId: Int64
    public let seasonNumber: Int64
    public let name: String
    public let watchedCount: Int32
    public let totalCount: Int32
    public let progressPercentage: Float

    public var isSeasonWatched: Bool {
        watchedCount == totalCount && totalCount > 0
    }

    public init(
        tvShowId: Int64,
        seasonId: Int64,
        seasonNumber: Int64,
        name: String,
        watchedCount: Int32 = 0,
        totalCount: Int32 = 0,
        progressPercentage: Float = 0
    ) {
        self.tvShowId = tvShowId
        self.seasonId = seasonId
        self.seasonNumber = seasonNumber
        self.name = name
        self.watchedCount = watchedCount
        self.totalCount = totalCount
        self.progressPercentage = progressPercentage
    }
}
