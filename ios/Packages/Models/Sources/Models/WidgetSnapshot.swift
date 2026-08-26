import Foundation

public struct WidgetSnapshot: Codable, Equatable {
    public let writtenAtMillis: Int64
    public let entries: [WidgetSnapshotEntry]

    public init(writtenAtMillis: Int64, entries: [WidgetSnapshotEntry]) {
        self.writtenAtMillis = writtenAtMillis
        self.entries = entries
    }

    public var writtenAt: Date {
        Date(timeIntervalSince1970: TimeInterval(writtenAtMillis) / 1000)
    }
}

public struct WidgetSnapshotEntry: Codable, Equatable, Identifiable {
    public var id: Int64 {
        tmdbId
    }

    public let tmdbId: Int64
    public let showName: String
    public let episodeName: String
    public let seasonNumber: Int64
    public let episodeNumber: Int64
    public let posterFileName: String?

    public init(
        tmdbId: Int64,
        showName: String,
        episodeName: String,
        seasonNumber: Int64,
        episodeNumber: Int64,
        posterFileName: String? = nil
    ) {
        self.tmdbId = tmdbId
        self.showName = showName
        self.episodeName = episodeName
        self.seasonNumber = seasonNumber
        self.episodeNumber = episodeNumber
        self.posterFileName = posterFileName
    }
}
