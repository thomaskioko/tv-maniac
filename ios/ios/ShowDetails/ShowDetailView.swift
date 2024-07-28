//
//  ShowDetailView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 13.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
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
            ErrorUiView(
                systemImage: "exclamationmark.triangle.fill",
                action: { component.dispatch(action: ReloadShowDetails()) }
            )
        } else if let showDetails = uiState.showDetails {
            ParallaxView(
                title: showDetails.title,
                isRefreshing: uiState.isUpdating || uiState.showInfo is ShowInfoStateLoading,
                imageHeight: DimensionConstants.imageHeight,
                collapsedImageHeight: DimensionConstants.collapsedImageHeight,
                header: { proxy in
                    let progress = proxy.titleOpacity(scrollOffset: proxy.frame(in: .global).minY, imageHeight: DimensionConstants.imageHeight, collapsedImageHeight: DimensionConstants.collapsedImageHeight)
                    let headerHeight = proxy.headerHeight(scrollOffset: proxy.frame(in: .global).minY)
                    
                    HeaderContentView(
                        show: showDetails,
                        progress: progress,
                        headerHeight: headerHeight
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

private struct DimensionConstants {
    static let imageHeight: CGFloat = 400
    static let collapsedImageHeight: CGFloat = 120.0
}
