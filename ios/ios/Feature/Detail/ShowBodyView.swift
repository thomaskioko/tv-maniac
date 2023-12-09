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
    @Namespace var animation
    @State private var detailShowId: Int64 = -1
    @State private var showView: Bool = false
    
    
    var seasonList: [Season]
    var trailerList: [Trailer]
    var similarShowsList: [Show]
    var onClick : (Int64) -> Void
    
    
    var body: some View {
        
        VStack(alignment: .leading) {
            if !seasonList.isEmpty {
                Text("Browse Seasons")
                    .titleSemiBoldFont(size: 23)
                    .foregroundColor(Color.text_color_bg)
                    .padding(.trailing, 16)
                    .padding(.leading, 16)
                    .padding(.top, 5)
                
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(alignment: .center) {
                        ForEach(seasonList, id: \.self) { season in
                            
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
            
            
            if !similarShowsList.isEmpty {
                Text("More like this")
                    .titleSemiBoldFont(size: 23)
                    .padding(.top, 8)
                    .foregroundColor(Color.text_color_bg)
                    .padding(.trailing, 16)
                    .padding(.leading, 16)
                
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(alignment: .top) {
                        ForEach(similarShowsList, id: \.traktId) { item in
                            ShowPosterImage(
                                posterSize: .medium,
                                imageUrl: item.posterImageUrl,
                                showTitle: item.title,
                                showId: item.traktId,
                                onClick: { onClick(item.traktId)}
                            )
                            
                        }
                    }
                    .padding(.trailing, 16)
                    .padding(.leading, 16)
                }
                
            }
            if !trailerList.isEmpty {
                //TODO::#93 Add show content
            }
        }
        .padding(.bottom, 220)
        .background(Color.background)
    }
    
}

struct ShowBodyView_Previews: PreviewProvider {
    static var previews: some View {
        ShowBodyView(
            seasonList: detailState.seasonsContent.seasonsList,
            trailerList: detailState.trailersContent.trailersList,
            similarShowsList: detailState.similarShowsContent.similarShows,
            onClick: { _ in }
        )
    }
}
