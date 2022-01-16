import SwiftUI
import TvManiac
import Kingfisher

struct ShowRow: View {

    var catagoryName: String
    var shows: [ShowUiModel]
    let processor = DownsamplingImageProcessor(size: CGSize(width: PosterStyle.Size.medium.width(), height: PosterStyle.Size.medium.height()))
            |> RoundCornerImageProcessor(cornerRadius: 5)

    var body: some View {
        VStack(alignment: .leading) {

            HStack {
                Text(self.catagoryName)
                        .bodyMediumFont(size: 23)
                        .foregroundColor(.yellow_300)
                        .padding(.top, 8)

                Spacer()

                NavigationLink(destination: ShowGridView()
                        .navigationBarTitle(self.catagoryName),
                        label: {
                            Spacer()
                            Text("More")
                                    .bodyMediumFont(size: 16)
                                    .foregroundColor(.accent_color)
                                    .padding(.top, 18)
                        })
            }

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .top) {
                    ForEach(self.shows, id: \.title) { show in
                        NavigationLink(destination: ShowDetailView(show: show)) {
                            ShowPosterImage(
                                    processor: processor,
                                    posterSize: .medium,
                                    imageUrl: show.posterImageUrl
                            )


                        }
                    }
                }
            }
        }
                .padding(.trailing, 16)
                .padding(.leading, 16)
    }
}

struct ShowRow_Previews: PreviewProvider {
    static var previews: some View {
        ShowRow(catagoryName: "Trending", shows: [
            ShowUiModel(
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
            ),
            ShowUiModel(
                    id: 2,
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
            ),
            ShowUiModel(
                    id: 3,
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
            )])
    }
}
