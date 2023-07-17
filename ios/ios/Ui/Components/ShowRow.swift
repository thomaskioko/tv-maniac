import SwiftUI
import TvManiac

struct ShowRow: View {
    
    @Namespace var animation
    var categoryName: String
    var shows: [TvShow]?

    var body: some View {
        VStack(alignment: .leading) {
            if(shows?.isEmpty != true){
                HStack {
                    Text(categoryName)
                        .titleSemiBoldFont(size: 20)
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
                        ForEach(shows!, id: \.title) { item in
                            ShowPosterImage(
                                posterSize: .medium,
                                imageUrl: item.posterImageUrl,
                                showTitle: item.title,
                                showId: item.traktId
                            )
                        }
                    }
                    .ignoresSafeArea()
                    .navigationBarHidden(true)
                    .padding(.trailing, 8)
                    .padding(.leading, 8)
                }
            }
        }
    }
}

struct ShowRow_Previews: PreviewProvider {
    static var previews: some View {
        ShowRow(categoryName: "Trending", shows: [mockTvShow,mockTvShow,mockTvShow])
    }
}
