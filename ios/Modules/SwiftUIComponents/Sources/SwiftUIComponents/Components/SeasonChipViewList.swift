//
//  SeasonChipViewList.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 4/4/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

public struct SeasonChipViewList: View {
    @Theme private var theme

    private let items: [SwiftSeason]
    private let onClick: (Int) -> Void

    public init(
        items: [SwiftSeason],
        onClick: @escaping (Int) -> Void
    ) {
        self.items = items
        self.onClick = onClick
    }

    public var body: some View {
        if !items.isEmpty {
            ChevronTitle(title: "Browse Seasons")

            ScrollView(.horizontal, showsIndicators: false) {
                HStack {
                    ForEach(items.indices, id: \.self) { index in
                        let season = items[index]

                        ChipView(
                            label: season.name,
                            action: { onClick(index) }
                        )
                    }
                }
                .padding([.trailing, .leading], theme.spacing.medium)
            }
        }
    }
}

#Preview {
    SeasonChipViewList(
        items: [
            .init(tvShowId: 23, seasonId: 23, seasonNumber: 1, name: "Season 1"),
            .init(tvShowId: 123, seasonId: 123, seasonNumber: 2, name: "Season 2"),
        ],
        onClick: { _ in }
    )
}
