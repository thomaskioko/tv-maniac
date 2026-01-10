//
//  HorizontalItemListView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct HorizontalItemListView: View {
    @Theme private var theme

    private let title: String
    private let subtitle: String
    private let chevronStyle: ChevronStyle
    private let items: [SwiftShow]
    private let onClick: (Int64) -> Void
    private let onMoreClicked: () -> Void

    public init(
        title: String,
        subtitle: String = "",
        chevronStyle: ChevronStyle = .none,
        items: [SwiftShow],
        onClick: @escaping (Int64) -> Void,
        onMoreClicked: @escaping () -> Void = {}
    ) {
        self.items = items
        self.title = title
        self.subtitle = subtitle
        self.onClick = onClick
        self.chevronStyle = chevronStyle
        self.onMoreClicked = onMoreClicked
    }

    public var body: some View {
        VStack {
            if !items.isEmpty {
                ChevronTitle(
                    title: title,
                    chevronStyle: chevronStyle,
                    action: onMoreClicked
                )

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack {
                        ForEach(items, id: \.traktId) { item in
                            PosterItemView(
                                title: item.title,
                                posterUrl: item.posterUrl,
                                isInLibrary: item.inLibrary
                            )
                            .padding([.leading, .trailing], theme.spacing.xxxSmall)
                            .padding(.leading, item.traktId == items.first?.traktId ? theme.spacing.small - 2 : 0)
                            .padding(.trailing, item.traktId == items.last?.traktId ? theme.spacing.xSmall : 0)
                            .onTapGesture { onClick(item.traktId) }
                        }
                    }
                }
            }
        }
        .padding(.bottom, theme.spacing.medium)
    }
}

#Preview {
    VStack {
        HorizontalItemListView(
            title: "Coming Soon",
            items: [
                .init(
                    traktId: 1234,
                    title: "Arcane",
                    posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "https://image.tmdb.org/t/p/w780/NNC08YmJFFlLi1prBkK8quk3dp.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 12346,
                    title: "Kaos",
                    posterUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            onClick: { _ in },
            onMoreClicked: {}
        )

        HorizontalItemListView(
            title: "Trending Today",
            chevronStyle: .chevronOnly,
            items: [
                .init(
                    traktId: 124,
                    title: "Terminator",
                    posterUrl: "https://image.tmdb.org/t/p/w780/woH18JkZMYhMSWqtHkPA4F6Gd1z.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 123_346,
                    title: "The Perfect Couple",
                    posterUrl: "https://image.tmdb.org/t/p/w780//3buRSGVnutw8x4Lww0t70k5dG6R.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 2346,
                    title: "One Piece",
                    posterUrl: "https://image.tmdb.org/t/p/w780/2rmK7mnchw9Xr3XdiTFSxTTLXqv.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            onClick: { _ in },
            onMoreClicked: {}
        )
    }
}
