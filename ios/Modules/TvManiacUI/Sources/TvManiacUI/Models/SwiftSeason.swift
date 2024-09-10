import Foundation

public struct SwiftSeason: Identifiable {
    public let id: UUID = UUID()
    public let tvShowId: Int64
    public let seasonId: Int64
    public let seasonNumber: Int64
    public let name: String

    public init(tvShowId: Int64, seasonId: Int64, seasonNumber: Int64, name: String) {
        self.tvShowId = tvShowId
        self.seasonId = seasonId
        self.seasonNumber = seasonNumber
        self.name = name
    }
}
