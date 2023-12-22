//
//  HorizontalShowsView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct HorizontalShowsListView: View {
    let title: String
    var subtitle = String()
    var items: [Show]
    var onClick : (Int64) -> Void
    
    var body: some View {
        if !items.isEmpty {
            VStack {
                TitleView(
                    title: title,
                    subtitle: subtitle,
                    showChevron: true
                )
                
                ScrollView(.horizontal, showsIndicators: false) {
                    LazyHStack {
                        ForEach(items, id: \.tmdbId) { item in
                            PosterItemView(
                                showId: item.tmdbId,
                                title: item.title,
                                posterUrl: item.posterImageUrl,
                                isInLibrary: item.isInLibrary
                            )
                            .padding(.leading, item.tmdbId == items.first?.tmdbId ? 16 : 0)
                            .padding(.trailing, item.tmdbId == items.last?.tmdbId ? 2 : 0)
                            .onTapGesture { onClick(item.tmdbId) }
                        }
                    }
                }
            }
        }
        
    }
}
