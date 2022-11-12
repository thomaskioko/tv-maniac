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
					.padding(.trailing, 16)
					.padding(.leading, 16)
				
				Spacer()
				
				NavigationLink(destination: ShowGridView()
					.navigationBarTitle(categoryName),
							   label: {
					Spacer()
					Text("More")
						.bodyMediumFont(size: 16)
						.foregroundColor(.accent_color)
						.padding(.top, 18)
						.padding(.trailing, 16)
						.padding(.leading, 16)
				})
			}
			
			ScrollView(.horizontal, showsIndicators: false) {
				HStack(alignment: .top) {
					ForEach(shows, id: \.title) { show in
						NavigationLink(destination: ShowDetailView(showId: show.traktId)) {
							ShowPosterImage(
								posterSize: .medium,
								imageUrl: show.posterImageUrl
							)
						}
					}
				}
				.padding(.trailing, 16)
				.padding(.leading, 16)
			}
		}
	}
}

struct ShowRow_Previews: PreviewProvider {
	static var previews: some View {
		ShowRow(categoryName: "Trending", shows: [mockShow,mockShow,mockShow])
	}
}
