//
//  HorizontalItemContentListView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct HorizontalItemContentListView: View {
    let items: [DiscoverShow]?
    let title: String
    var subtitle = String()
    var onClick : (Int64) -> Void
    
    var body: some View {
        if let items {
            if !items.isEmpty {
                VStack {
                    TitleView(title: title, subtitle: subtitle)
                        .padding(.leading, 8)
                    
                    ScrollView(.horizontal, showsIndicators: false) {
                        
                        LazyHStack {
                            ForEach(items, id: \.tmdbId) { item in
                                ItemContentPosterView(show: item)
                                    .padding([.leading, .trailing], 2)
                                    .padding(.leading, item.tmdbId == items.first?.tmdbId ? 16 : 0)
                                    .padding(.trailing, item.tmdbId == items.last?.tmdbId ? 8 : 0)
                                    .padding(.vertical)
                                    .onTapGesture { onClick(item.tmdbId) }
                            }
                        }
                    }
                }
            }
        }
    }
}
