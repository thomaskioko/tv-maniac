import SwiftUI
import TvManiac

struct ShowRow: View {

    var categoryName: String
    var shows: [TvShow]

    var body: some View {
        VStack(alignment: .leading) {

            HStack {
                Text(categoryName)
                        .titleSemiBoldFont(size: 23)
                        .padding(.top, 8)

                Spacer()

                NavigationLink(destination: ShowGridView()
                        .navigationBarTitle(self.categoryName),
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
                    ForEach(shows, id: \.title) { show in
                        NavigationLink(destination: ShowDetailView(showId: show.id)) {
                            ShowPosterImage(
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
        ShowRow(categoryName: "Trending", shows: [
			TvShow(
                    id: 1,
                    title: "",
                    overview: "",
                    language: "",
                    posterImageUrl: "https://image.tmdb.org/t/p/original/bZGAX8oMDm3Mo5i0ZPKh9G2OcaO.jpg",
                    backdropImageUrl: "",
                    year: "",
                    status: "",
                    votes: 21,
                    numberOfSeasons: 2,
                    numberOfEpisodes: 12,
                    averageVotes: 233.0,
					following: false,
                    genreIds: [12, 15]
            ),
			TvShow(
                    id: 2,
                    title: "",
                    overview: "",
                    language: "",
                    posterImageUrl: "https://image.tmdb.org/t/p/original/bZGAX8oMDm3Mo5i0ZPKh9G2OcaO.jpg",
                    backdropImageUrl: "",
                    year: "",
                    status: "",
                    votes: 21,
                    numberOfSeasons: 2,
                    numberOfEpisodes: 12,
                    averageVotes: 233.0,
					following: false,
                    genreIds: [12, 15]
            ),
			TvShow(
                    id: 3,
                    title: "",
                    overview: "",
                    language: "",
                    posterImageUrl: "https://image.tmdb.org/t/p/original/bZGAX8oMDm3Mo5i0ZPKh9G2OcaO.jpg",
                    backdropImageUrl: "",
                    year: "",
                    status: "",
                    votes: 21,
                    numberOfSeasons: 2,
                    numberOfEpisodes: 12,
                    averageVotes: 233.0,
					following: false,
                    genreIds: [12, 15]
            )])
    }
}
