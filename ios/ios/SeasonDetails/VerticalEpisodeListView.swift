//
//  EpisodeListView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct VerticalEpisodeListView: View {
    var items: [EpisodeDetailsModel]
    
    var body: some View {
        ScrollView(.vertical, showsIndicators: false) {
            LazyVStack{
                ForEach(items, id: \.id) { item in
                    EpisodeItemView(
                        imageUrl: item.imageUrl,
                        episodeTitle: item.episodeNumberTitle,
                        episodeOverView: item.overview
                    )
                        .padding(.top, item.id == items.first?.id ? 16 : 8)
                }
            }
        }
    }
}
