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
    private let selectedIndex: Int
    private let onClick: (Int) -> Void

    public init(
        items: [SwiftSeason],
        selectedIndex: Int = 0,
        onClick: @escaping (Int) -> Void
    ) {
        self.items = items
        self.selectedIndex = selectedIndex
        self.onClick = onClick
    }

    public var body: some View {
        if !items.isEmpty {
            ChevronTitle(title: "All Seasons")

            ScrollViewReader { proxy in
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack {
                        ForEach(items.indices, id: \.self) { index in
                            let season = items[index]

                            ChipView(
                                label: season.name,
                                isSelected: index == selectedIndex,
                                action: { onClick(index) }
                            )
                            .id(index)
                        }
                    }
                    .padding([.trailing, .leading], theme.spacing.medium)
                }
                .onAppear {
                    if selectedIndex > 0, selectedIndex < items.count {
                        proxy.scrollTo(selectedIndex, anchor: .center)
                    }
                }
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
