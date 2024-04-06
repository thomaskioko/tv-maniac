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

    @ObservedObject @StateFlow private var uiState: ShowDetailsState
    @State private var progress: CGFloat = 0

    init(presenter: ShowDetailsPresenter){
        self.presenter = presenter
        self._uiState = .init(presenter.state)
    }

    var body: some View {
        ZStack {
                ScalingHeaderScrollView {
                    HeaderContentView(
                        show: uiState.showDetails,
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
                    SeasonsRowView(
                        seasonsList: uiState.seasonsList,
                        onClick: { params in
                            presenter.dispatch(action: SeasonClicked(params: params))
                        }
                    )

                    ProvidersList(items: uiState.providers)

                    TrailerListView(trailers: uiState.trailersList, openInYouTube: uiState.openTrailersInYoutube)

                    CastListView(casts: uiState.castsList)

                    HorizontalShowsListView(
                        title: "Recommendations",
                        items: uiState.recommendedShowList,
                        onClick: { id in presenter.dispatch(action: DetailShowClicked(id: id)) }
                    )

                    HorizontalShowsListView(
                        title: "Similar Shows",
                        items: uiState.similarShows,
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
        .ignoresSafeArea()
    }

    private var recommendedShows: some View {
        VStack {
            TitleView(title: "Recommendations", showChevron: true)
        }
    }
}


