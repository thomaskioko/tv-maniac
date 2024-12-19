import Foundation
import TvManiac

public struct SwiftShow: Identifiable {
    public let id: UUID = .init()
    public let tmdbId: Int64
    public let title: String
    public let posterUrl: String?
    public let backdropUrl: String?
    public let inLibrary: Bool
    public let overview: String?

    public init(
        tmdbId: Int64,
        title: String,
        posterUrl: String?,
        backdropUrl: String? = nil,
        inLibrary: Bool,
        overview: String? = nil
    ) {
        self.tmdbId = tmdbId
        self.title = title
        self.posterUrl = posterUrl
        self.backdropUrl = backdropUrl
        self.inLibrary = inLibrary
        self.overview = overview
    }
}

public extension DiscoverShow {
  func toSwift() -> SwiftShow {
    .init(
      tmdbId: tmdbId,
      title: title,
      posterUrl: posterImageUrl,
      backdropUrl: nil,
      inLibrary: inLibrary,
      overview: overView
    )
  }
}
