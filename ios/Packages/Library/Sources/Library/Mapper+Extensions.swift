import Models
import TvManiac
import TvManiacKit

// MARK: - Library Mapping

public extension TvManiac.LibraryShowItem {
    func toSwift() -> SwiftLibraryItem {
        .init(
            showId: showId,
            title: title,
            posterUrl: posterImageUrl,
            year: year,
            status: status,
            seasonCount: seasonCount,
            episodeCount: episodeCount,
            rating: rating?.doubleValue,
            genres: genres?.map { String($0) },
            watchProviders: watchProviders.map { $0.toSwift() }
        )
    }
}
