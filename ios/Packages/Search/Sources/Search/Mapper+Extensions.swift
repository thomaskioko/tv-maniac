import TvManiac
import TvManiacKit

// MARK: - Search Mapping

public extension TvManiac.ShowItem {
    func toSwift() -> SwiftSearchShow {
        .init(
            tmdbId: tmdbId,
            showId: showId,
            title: title,
            overview: overview,
            status: status,
            imageUrl: posterImageUrl,
            year: year,
            voteAverage: voteAverage?.doubleValue,
            inLibrary: inLibrary,
            captionComponents: captionComponents(year: year, episodeCount: episodeCount.map { Int(truncating: $0) })
        )
    }
}

private func captionComponents(year: String?, episodeCount: Int?) -> [String] {
    let episodeCountText = episodeCount.map { String(\.plurals_search_episode_count, quantity: $0) }
    return [year, episodeCountText].compactMap { $0 }
}
