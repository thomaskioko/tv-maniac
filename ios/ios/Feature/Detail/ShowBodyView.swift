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
    var viewState: ShowDetailUiViewState

    var body: some View {

        VStack(alignment: .leading) {

            Text("Browse Seasons")
                    .titleSemiBoldFont(size: 23)
                    .foregroundColor(Color.text_color_bg)
                    .padding(.trailing, 16)
                    .padding(.leading, 16)
                    .padding(.top, 16)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .center) {
                    ForEach(viewState.seasonList, id: \.self) { season in

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

            if !viewState.episodeList.isEmpty {
                Text("Episodes")
                        .titleSemiBoldFont(size: 23)
                        .padding(.top, 8)
                        .foregroundColor(Color.text_color_bg)
                        .padding(.trailing, 16)
                        .padding(.leading, 16)

                ScrollView(.horizontal, showsIndicators: false) {

                    HStack(alignment: .center) {
                        ForEach(viewState.episodeList, id: \.self) { episode in
                            AirEpisodeCardView(episode: episode)
                        }
                    }
                            .padding(.leading, 8)
                            .padding(.bottom, 16)
                }
            }


            if !viewState.similarShowList.isEmpty {
                Text("More like this")
                        .titleSemiBoldFont(size: 23)
                        .padding(.top, 8)
                        .foregroundColor(Color.text_color_bg)
                        .padding(.trailing, 16)
                        .padding(.leading, 16)

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(alignment: .top) {

                        ForEach(viewState.similarShowList, id: \.self) { show in
                            NavigationLink(destination: ShowDetailView(showId: show.id)) {
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
                .padding(.bottom, 220)
                .background(Color.background)
    }

}

struct ShowBodyView_Previews: PreviewProvider {
    static var previews: some View {
        ShowBodyView(viewState: viewState)
    }
}
