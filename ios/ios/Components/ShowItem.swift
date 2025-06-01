
import SwiftUI
import TvManiac

struct ShowItem: View {
    var show: ShowUiModel

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            ShowPosterImage(
                imageLoader: ImageLoaderCache.shared.loaderFor(path: show.posterImageUrl, size: .original),
                posterSize: .medium
            )
        }
    }
}

struct ShowItem_Previews: PreviewProvider {
    static var previews: some View {
        ShowItem(show: ShowUiModel(
            id: 1,
            title: "",
            overview: "",
            language: "",
            posterImageUrl: "https://image.tmdb.org/t/p/original/bZGAX8oMDm3Mo5i0ZPKh9G2OcaO.jpg",
            backdropImageUrl: "",
            year: "",
            status: "",
            votes: 21,
            averageVotes: 233.0,
            isInWatchlist: false,
            genreIds: [12, 15]
        ))
    }
}
