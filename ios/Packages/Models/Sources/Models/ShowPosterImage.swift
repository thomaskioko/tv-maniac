import Foundation

public struct ShowPosterImage: Identifiable, Equatable {
    public var id: Int64 {
        showId
    }

    public let showId: Int64
    public let title: String
    public let posterUrl: String?
    public let inLibrary: Bool

    public init(
        showId: Int64,
        title: String,
        posterUrl: String?,
        inLibrary: Bool = false
    ) {
        self.showId = showId
        self.title = title
        self.posterUrl = posterUrl
        self.inLibrary = inLibrary
    }
}
