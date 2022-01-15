import SwiftUI
import TvManiac

struct ShowRow: View {
	
	var catagoryName: String
	var shows: [ShowUiModel]
	
	var body: some View {
		VStack(alignment: .leading) {
			
			HStack {
				
				LabelTitleText(text: self.catagoryName)
				
				Spacer()
				
				Button(action: {}) {
					LabelText(text: "More")
				}
			}
			
			ScrollView(.horizontal, showsIndicators: false) {
				HStack(alignment: .top) {
					ForEach(self.shows,id: \.title) { show in
						NavigationLink(destination: ShowDetailView(show: show)) {
							ShowItem(show: show)
						}
					}
				}
			}
		}
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
				genreIds: [12,15]
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
				genreIds: [12,15]
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
				genreIds: [12,15]
			)])
    }
}
