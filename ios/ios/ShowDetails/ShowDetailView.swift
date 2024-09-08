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
                    ShowInfoView(
                        loadedState: uiState.showInfo,
                        show: showDetails,
                        component: component,
                        titleRect: titleRect
                    )
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
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 480
    static let collapsedImageHeight: CGFloat = 120.0
}
