//
//  ShowDetailView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 13.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac
import ScalingHeaderScrollView

struct ShowDetailView: View {

    private let maxHeight : CGFloat = 520
    private let minHeight = 120.0
    private let presenter: ShowDetailsPresenter

    @ObservedObject
    private var uiState: StateFlow<ShowDetailsState>
    @State
    var progress: CGFloat = 0

    init(presenter: ShowDetailsPresenter){
        self.presenter = presenter
        self.uiState = StateFlow<ShowDetailsState>(presenter.state)
    }

    var body: some View {
        ZStack {
            if let state = uiState.value {
                ScalingHeaderScrollView {
                    HeaderContentView(
                        show: state.showDetails,
                        progress: progress,
                        maxHeight: maxHeight,
                        onAddToLibraryClick: { add in
                            presenter.dispatch(action: FollowShowClicked(addToLibrary: add))
                        },
                        onWatchTrailerClick: { id in
                            presenter.dispatch(action: WatchTrailerClicked(id: id))
                        }
                    )

                } content: {
                   seasonContent

                    ProvidersList(items: state.providers)

                    TrailerListView(trailers: state.trailersList, openInYouTube: state.openTrailersInYoutube)

                    CastListView(casts: state.castsList)

                    HorizontalShowsListView(
                        title: "Recommendations",
                        items: state.recommendedShowList,
                        onClick: { id in presenter.dispatch(action: DetailShowClicked(id: id)) }
                    )

                    HorizontalShowsListView(
                        title: "Similar Shows",
                        items: state.similarShows,
                        onClick: { id in presenter.dispatch(action: DetailShowClicked(id: id)) }
                    )

                }
                .height(min: minHeight, max: maxHeight)
                .collapseProgress($progress)
                .allowsHeaderGrowth()
                .hideScrollIndicators()
                .shadow(radius: progress)

                TopBar(onBackClicked: { presenter.dispatch(action: DetailBackClicked()) })

            }
        }
        .ignoresSafeArea()
    }


    private var seasonContent: some View {
        VStack {
            if !uiState.seasonsList.isEmpty {
                let seasons = uiState.seasonsList
                TitleView(
                    title: "Browse Seasons",
                    showChevron: false
                )

                ScrollView(.horizontal, showsIndicators: false) {
                    LazyHStack {
                        ForEach(uiState.seasonsList.indices, id: \.self) { index in
                            let season = seasons[index]

                            Button(
                                action: {
                                presenter.dispatch(
                                    action: SeasonClicked(
                                        params: ShowSeasonDetailsParam(
                                            showId: season.tvShowId,
                                            seasonId: season.seasonId,
                                            seasonNumber: season.seasonNumber,
                                            selectedSeasonIndex: Int32(index)
                                        )
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

    private var recommendedShows: some View {
        VStack {
            TitleView(title: "Recommendations", showChevron: true)
        }
    }
}


