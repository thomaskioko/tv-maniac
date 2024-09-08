//
//  HorizontalItemContentListView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac

struct HorizontalItemContentListView: View {
    let items: [DiscoverShow]?
    let title: String
    var subtitle = String()
    var onClick: (Int64) -> Void
    var onMoreClicked: () -> Void

    var body: some View {
        if let items {
            if !items.isEmpty {
                VStack {
                    ChevronTitle(
                        title: title,
                        action: { onMoreClicked() }
                    )
                    
                    ScrollView(.horizontal, showsIndicators: false) {
                        LazyHStack {
                            ForEach(items, id: \.tmdbId) { item in
                                PosterItemView(
                                    title: item.title,
                                    posterUrl: item.posterImageUrl,
                                    isInLibrary: item.inLibrary
                                )
                                .padding([.leading, .trailing], 2)
                                .padding(.leading, item.tmdbId == items.first?.tmdbId ? 10 : 0)
                                .padding(.trailing, item.tmdbId == items.last?.tmdbId ? 8 : 0)
                                .onTapGesture { onClick(item.tmdbId) }
                            }
                        }
                    }
                }
                .padding(.bottom)
            }
        }
    }
}
