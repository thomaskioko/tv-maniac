import Foundation
import TvManiac

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

public extension TvManiac.WatchlistItem {
  func toSwift() -> ShowPosterImage {
    .init(
      tmdbId: tmdbId,
      title: title,
      posterUrl: posterImageUrl,
      inLibrary: true
    )
  }
}

public extension SeasonImagesModel {
  func toSwift() -> ShowPosterImage {
    .init(
      tmdbId: id,
      title: "",
      posterUrl: imageUrl,
      inLibrary: false
    )
  }
}

public extension TvManiac.TvShow {
  func toSwift() -> ShowPosterImage {
    .init(
      tmdbId: tmdbId,
      title: title,
      posterUrl: posterImageUrl,
      inLibrary: inLibrary
    )
  }
}
