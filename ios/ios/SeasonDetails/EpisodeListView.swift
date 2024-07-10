//
//  EpisodeListView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/9/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import TvManiac

struct EpisodeListView : View {

    @State private var showingAlert: Bool = false
    let state: SeasonDetailsContent
    let onEpisodeHeaderClicked: () -> Void
    let onWatchedStateClicked: () -> Void

    var body: some View {
        VStack {
            Collapsible(
                episodeCount: state.episodeCount,
                watchProgress: CGFloat(state.watchProgress),
                isCollapsed: state.expandEpisodeItems,
                onCollapseClicked: onEpisodeHeaderClicked,
                onWatchedStateClicked: {
                    onWatchedStateClicked()
                    showingAlert = !state.showSeasonWatchStateDialog
                }
            ) {
                VStack {
                    VerticalEpisodeListView(items: state.episodeDetailsList)
                }
            }
            .alert(isPresented: $showingAlert, content: {
                let title = state.isSeasonWatched ? "Mark as unwatched" : "Mark as watched"
                let messageBody = state.isSeasonWatched ?
                "Are you sure you want to mark the entire season as unwatched?" : "Are you sure you want to mark the entire season as watched?"
                return Alert(
                    title: Text(title),
                    message: Text(messageBody),
                    primaryButton: .default(Text("No")) {


                    },
                    secondaryButton: .default(Text("Yes"))
                )
            })
        }
    }

    @ViewBuilder
    func VerticalEpisodeListView(items: [EpisodeDetailsModel]) -> some View {
        VStack{
            ScrollView(.vertical, showsIndicators: false) {
                LazyVStack{
                    ForEach(items, id: \.id) { item in
                        EpisodeItemView(
                            imageUrl: item.imageUrl,
                            episodeTitle: item.episodeNumberTitle,
                            episodeOverView: item.overview
                        )
                        .padding(.top, item.id == items.first?.id ? 16 : 8)
                    }
                }
            }
        }
    }
}
