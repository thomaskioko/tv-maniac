import Foundation

public struct SwiftShowGenre: Identifiable {
    public let id: UUID = .init()
    public let showId: Int64
    public let tmdbId: Int64
    public let name: String
    public let imageUrl: String?

    public init(showId: Int64, tmdbId: Int64, name: String, imageUrl: String?) {
        self.showId = showId
        self.tmdbId = tmdbId
        self.name = name
        self.imageUrl = imageUrl
    }
}
