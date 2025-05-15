//
//  RootNavigationView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 12/8/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiacKit

struct RootNavigationView: View {
  private let rootPresenter: RootPresenter
  @StateObject @KotlinStateFlow private var themeState: ThemeState
  @StateObject private var store = SettingsAppStorage.shared

  init(rootPresenter: RootPresenter) {
    self.rootPresenter = rootPresenter
    _themeState = .init(rootPresenter.themeState)
  }

  var body: some View {
    SplashView {
      DecomposeNavigationView(
        stack: rootPresenter.childStack,
        onBack: rootPresenter.onBackClicked,
        content: { child in
          switch onEnum(of: child) {
          case let .home(child):
            TabBarView(presenter: child.presenter)
          case let .showDetails(child):
            ShowDetailsView(presenter: child.presenter)
          case let .seasonDetails(child):
            SeasonDetailsView(presenter: child.presenter)
          case let .moreShows(child):
            EmptyView()
          case let .trailers(child):
            EmptyView()
          case let .genreShows(child):
            EmptyView()
          }
        }
      )
    }
    .appTheme()
    .onChange(of: themeState.appTheme) { newTheme in
      store.appTheme = newTheme.toDeveiceAppTheme()
    }
  }
}
