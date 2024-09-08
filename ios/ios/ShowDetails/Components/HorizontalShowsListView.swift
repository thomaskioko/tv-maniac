//
//  HorizontalShowsView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac

struct HorizontalShowsListView: View {
    private let title: String
    private var subtitle: String?
    private var items: [Show]
    private var onClick: (Int64) -> Void

    init(
        title: String,
        subtitle: String? = nil,
        items: [Show],
        onClick: @escaping (Int64) -> Void
    ) {
        self.title = title
        self.subtitle = subtitle
        self.items = items
        self.onClick = onClick
    }

    var body: some View {
        if !items.isEmpty {
            VStack {
                ChevronTitle(
                    title: title,
                    subtitle: subtitle,
                    chevronStyle: .chevronOnly
                )

                ScrollView(.horizontal, showsIndicators: false) {
                    LazyHStack {
                        ForEach(items, id: \.tmdbId) { item in
                            PosterItemView(
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
