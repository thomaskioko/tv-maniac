//
//  SeasonsRowView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 4/4/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct SeasonsRowView: View {
    var seasonsList: [Season]
    var onClick: (ShowSeasonDetailsParam) -> Void

    var body: some View {
        if !seasonsList.isEmpty {
            TitleView(
                title: "Browse Seasons",
                showChevron: false
            )

            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack {
                    ForEach(seasonsList.indices, id: \.self) { index in
                        let season = seasonsList[index]

                        Button(
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
                        ){
                            ChipView(label: season.name)
                        }

                    }
                }
                .padding(.trailing, 16)
                .padding(.leading, 16)
            }
        }
    }
}
