//
//  ShowBodyView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 16.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct ShowBodyView: View {

    @Environment(\.colorScheme) var scheme
    var detailLoadedState: ShowDetailsStateShowDetailsLoaded

    var body: some View {

        VStack(alignment: .leading) {
            if let state = detailLoadedState.seasonState as? SeasonStateSeasonsLoaded {
                Text("Browse Seasons")
                        .titleSemiBoldFont(size: 23)
                        .foregroundColor(Color.text_color_bg)
                        .padding(.trailing, 16)
                        .padding(.leading, 16)
                        .padding(.top, 5)

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(alignment: .center) {
                        ForEach(state.seasonsList, id: \.self) { season in

                            Button(action: {}) {
                                Text(season.name)
                                        .bodyMediumFont(size: 16)
                                        .foregroundColor(Color.accent)
                                        .padding(10)
                                        .background(Color.accent.opacity(0.12))
                                        .cornerRadius(5)
                            }

                        }
                    }
                            .padding(.trailing, 16)
                            .padding(.leading, 16)
                }
            }

            if let state = detailLoadedState.similarShowsState as? SimilarShowsStateSimilarShowsLoaded {
                if !state.similarShows.isEmpty {
                    Text("More like this")
                            .titleSemiBoldFont(size: 23)
                            .padding(.top, 8)
                            .foregroundColor(Color.text_color_bg)
                            .padding(.trailing, 16)
                            .padding(.leading, 16)

                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(alignment: .top) {
                            ForEach(state.similarShows, id: \.traktId) { show in
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
            if let state = detailLoadedState.trailerState as? TrailersStateTrailersLoaded {

            }
            if let state = detailLoadedState.trailerState as? TrailersStateTrailersError {

            }
        }
                .padding(.bottom, 220)
                .background(Color.background)
    }

}

struct ShowBodyView_Previews: PreviewProvider {
    static var previews: some View {
        ShowBodyView(detailLoadedState: detailState)
    }
}
