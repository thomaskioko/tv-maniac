//
//  SeasonsRowView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 4/4/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac

struct SeasonsRowView: View {
    private let seasonsList: [Season]
    private let onClick: (ShowSeasonDetailsParam) -> Void

    init(
        seasonsList: [Season],
        onClick: @escaping (ShowSeasonDetailsParam) -> Void
    ) {
        self.seasonsList = seasonsList
        self.onClick = onClick
    }

    var body: some View {
        if !seasonsList.isEmpty {
            ChevronTitle(title: "Browse Seasons")

            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack {
                    ForEach(seasonsList.indices, id: \.self) { index in
                        let season = seasonsList[index]

                        ChipView(
                            label: season.name,
                            action: {
                                onClick(
                                    ShowSeasonDetailsParam(
                                        showId: season.tvShowId,
                                        seasonId: season.seasonId,
                                        seasonNumber: season.seasonNumber,
                                        selectedSeasonIndex: Int32(index)
                                    )
                                )
                            }
                        )
                    }
                }
                .padding([.trailing, .leading], 16)
            }
        }
    }
}
