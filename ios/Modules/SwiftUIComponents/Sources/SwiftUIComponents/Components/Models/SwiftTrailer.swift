import Foundation

public struct SwiftTrailer: Identifiable {
    public let id = UUID()
    public let showTmdbId: Int64
    public let key: String
    public let name: String
    public let youtubeThumbnailUrl: String

    public init(showTmdbId: Int64, key: String, name: String, youtubeThumbnailUrl: String) {
        self.showTmdbId = showTmdbId
        self.key = key
        self.name = name
        self.youtubeThumbnailUrl = youtubeThumbnailUrl
    }
}
