//
//  CastListView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 20.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct CastListView: View {
    @Theme private var theme

    private let casts: [SwiftCast]

    public init(casts: [SwiftCast]) {
        self.casts = casts
    }

    public var body: some View {
        VStack(alignment: .leading) {
            if !casts.isEmpty {
                ChevronTitle(title: "Cast")

                ScrollView(.horizontal, showsIndicators: false) {
                    LazyHStack {
                        ForEach(casts, id: \.castId) { cast in
                            CastCardView(
                                profileUrl: cast.profileUrl,
                                name: cast.name,
                                characterName: cast.characterName
                            )
                            .padding([.leading, .trailing], theme.spacing.xxSmall)
                            .padding(.leading, cast.id == casts.first?.id ? theme.spacing.medium : 0)
                        }
                    }
                    .padding([.bottom, .trailing], theme.spacing.medium)
                }
            }
        }
    }
}

#Preview {
    VStack {
        CastListView(
            casts: [
                .init(
                    castId: 123,
                    name: "Rosario Dawson",
                    characterName: "Claire Temple",
                    profileUrl: "https://image.tmdb.org/t/p/w780/1mm7JGHIUX3GRRGXEV9QCzsI0ao.jpg"
                ),
                .init(
                    castId: 1234,
                    name: "Hailee Steinfeld",
                    characterName: "Hailee Steinfeld",
                    profileUrl: "https://image.tmdb.org/t/p/w780/6aBclBl8GMcxbxr6XcwSGg3IBea.jpg"
                ),
                .init(
                    castId: 1235,
                    name: "内田夕夜",
                    characterName: "Yuuya Uchida",
                    profileUrl: "https://image.tmdb.org/t/p/w780/4xLLQGEDWtmLWUapo0UnfvCdsXp.jpg"
                ),
            ]
        )
    }
}
