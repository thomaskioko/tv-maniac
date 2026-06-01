import Foundation

public struct SwiftShowGenre: Identifiable {
    public let id: UUID = .init()
    public let traktId: Int64
    public let tmdbId: Int64
    public let name: String
    public let imageUrl: String?

    public init(traktId: Int64, tmdbId: Int64, name: String, imageUrl: String?) {
        self.traktId = traktId
        self.tmdbId = tmdbId
        self.name = name
        self.imageUrl = imageUrl
    }
}
