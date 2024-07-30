//
//  RootView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 03.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct RootView: View {
    private let rootComponent: RootComponent
    @StateFlow private var uiState: ThemeState
    @State private var isShowingSplash = true

    init(rootComponent: RootComponent) {
        self.rootComponent = rootComponent
        _uiState = StateFlow(rootComponent.themeState)
    }

    var body: some View {
        ZStack {
            if isShowingSplash {
                SplashScreenView()
            } else {
                StackView(
                    stack: StateFlow(rootComponent.stack),
                    onBack: rootComponent.onBackClicked,
                    content: { child in
                        childView(for: child)
                    }
                )
                .environment(\.colorScheme, uiState.appTheme == .lightTheme ? .light : .dark)
            }
        }
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { // Adjust the delay as needed
                withAnimation {
                    isShowingSplash = false
                }
            }
        }
    }

    @ViewBuilder
    private func childView(for child: RootComponentChild) -> some View {
        switch onEnum(of: child) {
            case .home(let child):
                HomeTabView(component: child.component)
            case .showDetails(let child):
                ShowDetailView(component: child.component)
            case .seasonDetails(let child):
                SeasonDetailsView(component: child.component)
            case .moreShows(let child):
                MoreShowsView(component: child.component)
            case .trailers(_):
                EmptyView() //TODO:: Add implementation
        }
    }
}
