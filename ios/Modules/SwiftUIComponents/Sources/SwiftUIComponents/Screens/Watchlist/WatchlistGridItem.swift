import SwiftUI

public struct WatchlistGridItem: Identifiable, Equatable {
    public var id: Int64 {
        traktId
    }

    public let traktId: Int64
    public let title: String
    public let posterImageUrl: String?
    public let watchProgress: Float

    public init(traktId: Int64, title: String, posterImageUrl: String?, watchProgress: Float) {
        self.traktId = traktId
        self.title = title
        self.posterImageUrl = posterImageUrl
        self.watchProgress = watchProgress
    }
}

enum WatchlistScreenConstants {
    static let spacing: CGFloat = 4
    static let columns: [GridItem] = [
        GridItem(.adaptive(minimum: 100), spacing: spacing),
    ]
}
