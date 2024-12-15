//
//  RootNavigationView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 12/8/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac
import TvManiacKit

struct RootNavigationView: View {
  private let rootPresenter: RootPresenter
  @StateObject @KotlinStateFlow private var themeState: ThemeState
  
  init(rootPresenter: RootPresenter) {
    self.rootPresenter = rootPresenter
    _themeState = .init(rootPresenter.themeState)
  }
  
  var body: some View {
    SplashView {
      DecomposeNavigationView(
        stack: rootPresenter.childStack,
        onBack: rootPresenter.onBackClicked,
        content:  { child in
          switch onEnum(of: child) {
            case .home(let child) :
              HomeTabNavigationView(presenter: child.presenter)
            case .showDetails(let child):
              EmptyView()
            case .seasonDetails(let child):
              EmptyView()
            case .moreShows(let child):
              EmptyView()
            case .trailers(let child):
              EmptyView()
            case .genreShows(let child):
              EmptyView()
          }
        }
      )
    }
    .environment(\.colorScheme, themeState.appTheme == .lightTheme ? .light : .dark)
  }
}
