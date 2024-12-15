import Foundation
import TvManiac
public struct SwiftShowGenre: Identifiable {
    public let id: UUID = UUID()
    public let tmdbId: Int64
    public let name: String
    public let imageUrl: String?
    
    public init(tmdbId: Int64, name: String, imageUrl: String?) {
        self.tmdbId = tmdbId
        self.name = name
        self.imageUrl = imageUrl
    }
}

public extension TvManiac.ShowGenre {
  func toSwift() -> SwiftShowGenre {
    .init(tmdbId: id, name: name, imageUrl: posterUrl)
  }
}
