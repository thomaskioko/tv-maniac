import Foundation

public struct SwiftTrailer: Identifiable, Equatable {
    public var id: String {
        key
    }

    public let showId: Int64
    public let key: String
    public let name: String
    public let youtubeThumbnailUrl: String

    public init(showId: Int64, key: String, name: String, youtubeThumbnailUrl: String) {
        self.showId = showId
        self.key = key
        self.name = name
        self.youtubeThumbnailUrl = youtubeThumbnailUrl
    }
}
