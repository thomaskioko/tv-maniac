import TvManiac

// MARK: - Search Mapping

public extension TvManiac.ShowItem {
    func toSwift() -> SwiftSearchShow {
        .init(
            tmdbId: tmdbId,
            traktId: traktId,
            title: title,
            overview: overview,
            status: status,
            imageUrl: posterImageUrl,
            year: year,
            voteAverage: voteAverage?.doubleValue
        )
    }
}
