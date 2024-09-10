//
//  ShowDetailView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 13.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacUI

struct ShowDetailView: View {
    private let component: ShowDetailsComponent

    @StateFlow private var uiState: ShowDetailsContent
    @State private var scrollOffset: CGFloat = 0

    init(component: ShowDetailsComponent) {
        self.component = component
        _uiState = StateFlow(component.state)
    }

    var body: some View {
        if !uiState.isUpdating && uiState.showDetails == nil {
            FullScreenView(
                buttonText: "Retry",
                action: { component.dispatch(action: ReloadShowDetails()) }
            )
        } else if let showDetails = uiState.showDetails {
            ParallaxView(
                title: showDetails.title,
                isRefreshing: uiState.isUpdating || uiState.showInfo is ShowInfoStateLoading,
                imageHeight: DimensionConstants.imageHeight,
                collapsedImageHeight: DimensionConstants.collapsedImageHeight,
                header: { proxy in
                    HeaderView(
                        title: showDetails.title,
                        overview: showDetails.overview,
                        backdropImageUrl: showDetails.backdropImageUrl,
                        status: showDetails.status,
                        year: showDetails.year,
                        language: showDetails.language,
                        rating: showDetails.rating,
                        progress: proxy.getTitleOpacity(
                            geometry: proxy,
                            imageHeight: DimensionConstants.imageHeight,
                            collapsedImageHeight: DimensionConstants.collapsedImageHeight
                        ),
                        headerHeight: proxy.getHeightForHeaderImage(proxy)
                    )
                },
                content: { titleRect in
                    ShowInfoContent(show: showDetails, titleRect: titleRect)
                },
                onBackClicked: {
                    component.dispatch(action: DetailBackClicked())
                },
                onRefreshClicked: {
                    component.dispatch(action: ReloadShowDetails())
                }
            )
        }
    }

    @ViewBuilder
    func ShowInfoContent(show: ShowDetails, titleRect: Binding<CGRect>) -> some View {
        switch onEnum(of: uiState.showInfo) {
            case .loading, .empty:
                LoadingIndicatorView(animate: true)
            case .error:
                FullScreenView(
                    buttonText: "Retry",
                    action: { component.dispatch(action: ReloadShowDetails()) }
                )
            case .loaded(let state):
                ShowInfoView(
                    isFollowed: show.isFollowed,
                    openTrailersInYoutube: state.openTrailersInYoutube,
                    genreList: show.genres.map { $0.toSwift() },
                    seasonList: state.seasonsList.map { $0.toSwift() },
                    providerList: state.providers.map { $0.toSwift() },
                    trailerList: state.trailersList.map { $0.toSwift() },
                    castsList: state.castsList.map { $0.toSwift() },
                    recommendedShowList: state.recommendedShowList.map { $0.toSwift() },
                    similarShows: state.similarShows.map { $0.toSwift() },
                    onWatchTrailer: {
                        component.dispatch(action: WatchTrailerClicked(id: show.tmdbId))
                    },
                    onAddToLibrary: {
                        component.dispatch(action: FollowShowClicked(addToLibrary: show.isFollowed))
                    },
                    onSeasonClicked: { index, season in
                        let params = ShowSeasonDetailsParam(
                            showId: season.tvShowId,
                            seasonId: season.seasonId,
                            seasonNumber: season.seasonNumber,
                            selectedSeasonIndex: Int32(index)
                        )

                        component.dispatch(action: SeasonClicked(params: params))
                    },
                    onShowClicked: {
                        component.dispatch(action: FollowShowClicked(addToLibrary: show.isFollowed))
                    },
                    titleRect: titleRect
                )
        }
    }
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 480
    static let collapsedImageHeight: CGFloat = 120.0
}
