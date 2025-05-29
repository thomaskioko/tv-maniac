import Foundation

public struct SwiftShowGenre: Identifiable {
    public let id: UUID = .init()
    public let tmdbId: Int64
    public let name: String
    public let imageUrl: String?

    public init(tmdbId: Int64, name: String, imageUrl: String?) {
        self.tmdbId = tmdbId
        self.name = name
        self.imageUrl = imageUrl
    }
}
