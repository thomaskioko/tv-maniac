import Components
import DesignSystem
import Models
import SwiftUI

public struct MyShowsGridItem: Identifiable, Equatable {
    public var id: Int64 {
        showId
    }

    public let showId: Int64
    public let title: String
    public let posterImageUrl: String?
    public let watchProgress: Float

    public init(showId: Int64, title: String, posterImageUrl: String?, watchProgress: Float) {
        self.showId = showId
        self.title = title
        self.posterImageUrl = posterImageUrl
        self.watchProgress = watchProgress
    }
}
