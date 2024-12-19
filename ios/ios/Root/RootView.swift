//
//  RootView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 03.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TvManiac
import TvManiacKit

struct RootView: View {
  private let rootPresenter: RootPresenter
  @StateFlow private var uiState: ThemeState
  @State private var isShowingSplash = true
  
  init(rootPresenter: RootPresenter) {
    self.rootPresenter = rootPresenter
    _uiState = StateFlow(rootPresenter.themeState)
  }
  
  var body: some View {
    ZStack {
      if isShowingSplash {
        SplashScreenView()
      } else {
        DecomposeNavigationStack(
          childStack: rootPresenter.childStack,
          onBack: rootPresenter.onBackClicked,
          content: { child in
            childView(for: child)
          }
        )
        .appTheme(uiState.appTheme.toDeveiceAppTheme())
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
  private func childView(for child: RootPresenterChild) -> some View {
    switch onEnum(of: child) {
      case .home(let child):
        HomeTabView(presenter: child.presenter)
      case .showDetails(let child):
        ShowDetailView(presenter: child.presenter)
      case .seasonDetails(let child):
        SeasonDetailsView(presenter: child.presenter)
      case .moreShows(let child):
        MoreShowsView(presenter: child.presenter)
      case .trailers:
        EmptyView() // TODO: Add implementation
      case .genreShows:
        EmptyView()
    }
  }
}
