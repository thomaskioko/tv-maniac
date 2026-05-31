import Foundation

public struct ShowPosterImage: Identifiable, Equatable {
    public var id: Int64 {
        traktId
    }

    public let traktId: Int64
    public let title: String
    public let posterUrl: String?
    public let inLibrary: Bool

    public init(
        traktId: Int64,
        title: String,
        posterUrl: String?,
        inLibrary: Bool = false
    ) {
        self.traktId = traktId
        self.title = title
        self.posterUrl = posterUrl
        self.inLibrary = inLibrary
    }
}
