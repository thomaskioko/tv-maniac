//
//  TabView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/23/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac

struct HomeTabView: View {
  private let presenter: HomePresenter
  @StateFlow private var stack: ChildStack<AnyObject, HomePresenterChild>
  private var activeChild: HomePresenterChild { stack.active.instance }

  init(presenter: HomePresenter) {
    self.presenter = presenter
    _stack = StateFlow(presenter.stack)
  }

  var body: some View {
    VStack {
      HomeChildView(
        screen: activeChild,
        bottomTabActions: bottomTabActions()
      )
    }
  }

  private func bottomTabActions() -> [BottomTabAction] {
    return [
      BottomTabAction(
        title: "Discover",
        systemImage: "tv",
        isActive: activeChild is HomePresenterChildDiscover,
        action: { presenter.onDiscoverClicked() }
      ),
      BottomTabAction(
        title: "Search",
        systemImage: "magnifyingglass",
        isActive: activeChild is HomePresenterChildSearch,
        action: { presenter.onSearchClicked() }
      ),
      BottomTabAction(
        title: "Library",
        systemImage: "list.bullet.below.rectangle",
        isActive: activeChild is HomePresenterChildLibrary,
        action: { presenter.onLibraryClicked() }
      ),
      BottomTabAction(
        title: "Settings",
        systemImage: "gearshape",
        isActive: activeChild is HomePresenterChildSettings,
        action: { presenter.onSettingsClicked() }
      )
    ]
  }
}
