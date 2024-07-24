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

    @StateFlow private var uiState: ShowDetailsContent
    @State var progress: CGFloat = 0

    init(presenter: ShowDetailsPresenter){
        self.presenter = presenter
        _uiState = StateFlow(presenter.state)
    }

    var body: some View {
        ZStack {
            ScalingHeaderScrollView {
                if (!uiState.isUpdating && uiState.showDetails == nil){
                    ErrorUiView(
                        systemImage: "exclamationmark.triangle.fill",
                        action: {  presenter.dispatch(action: ReloadShowDetails()) }
                    )
                }
                else {
                    if let showDetails = uiState.showDetails {
                        HeaderContentView(
                            show: showDetails,
                            progress: progress,
                            maxHeight: maxHeight,
                            onAddToLibraryClick: { add in
                                presenter.dispatch(action: FollowShowClicked(addToLibrary: add))
                            },
                            onWatchTrailerClick: { id in
                                presenter.dispatch(action: WatchTrailerClicked(id: id))
                            }
                        )
                    }

                }
            } content: {
                switch onEnum(of: uiState.showInfo){
                    case .loading: LoadingIndicatorView(animate: true)
                    case .empty:  ErrorUiView(
                        systemImage: "exclamationmark.triangle.fill",
                        action: {  presenter.dispatch(action: ReloadShowDetails()) }
                    )
                    case .error :  ErrorUiView(
                        systemImage: "exclamationmark.triangle.fill",
                        action: {  presenter.dispatch(action: ReloadShowDetails()) }
                    )
                    case .loaded(let loadedState): ShowInfoView(loadedState)
                }
            }
            .height(min: minHeight, max: maxHeight)
            .collapseProgress($progress)
            .allowsHeaderGrowth()
            .hideScrollIndicators()
            .shadow(radius: progress)

            TopBar(
                progress: progress,
                title: uiState.showDetails?.title ?? "",
                isRefreshing: uiState.isUpdating || uiState.showInfo is ShowInfoStateLoading,
                onBackClicked: {
                    presenter.dispatch(action: DetailBackClicked())
                },
                onRefreshClicked: {
                    presenter.dispatch(action: ReloadShowDetails())
                }
            )
        }
        .ignoresSafeArea()
        .background(Color.background)
    }

    @ViewBuilder
    func ShowInfoView(_ showInfo: ShowInfoStateLoaded) -> some View {

        SeasonsRowView(
            seasonsList: showInfo.seasonsList,
            onClick: { params in
                presenter.dispatch(action: SeasonClicked(params: params))
            }
        )

        ProvidersList(items: showInfo.providers)

        TrailerListView(trailers: showInfo.trailersList, openInYouTube: showInfo.openTrailersInYoutube)

        CastListView(casts: showInfo.castsList)

        HorizontalShowsListView(
            title: "Recommendations",
            items: showInfo.recommendedShowList,
            onClick: { id in presenter.dispatch(action: DetailShowClicked(id: id)) }
        )

        HorizontalShowsListView(
            title: "Similar Shows",
            items: showInfo.similarShows,
            onClick: { id in presenter.dispatch(action: DetailShowClicked(id: id)) }
        )
    }
}


