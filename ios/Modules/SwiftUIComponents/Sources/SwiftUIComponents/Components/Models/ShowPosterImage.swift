import Foundation

public struct ShowPosterImage: Identifiable {
    public let id: UUID = .init()
    public let tmdbId: Int64
    public let title: String
    public let posterUrl: String?
    public let inLibrary: Bool

    public init(
        tmdbId: Int64,
        title: String,
        posterUrl: String?,
        inLibrary: Bool = false
    ) {
        self.tmdbId = tmdbId
        self.title = title
        self.posterUrl = posterUrl
        self.inLibrary = inLibrary
    }
}
