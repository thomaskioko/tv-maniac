import SwiftUI
import TvManiac
import TvManiacKit

public struct TabBarView: View {
  private let presenter: HomePresenter
  @StateObject @KotlinStateFlow private var stack: ChildStack<AnyObject, HomePresenterChild>
  @State private var selectedTab: NavigationTab = .discover

  init(presenter: HomePresenter) {
    self.presenter = presenter
    _stack = .init(presenter.homeChildStack)
  }

  public var body: some View {
    TabView(selection: $selectedTab) {
      ForEach(NavigationTab.allCases, id: \.self) { tab in
        TabContentView(
          child: stack.items.first(where: { tabForChild($0.instance) == tab })?.instance,
          tab: tab
        ) { child in
          switch onEnum(of: child) {
          case let .discover(screen):
            DiscoverTab(presenter: screen.presenter)
          case let .search(screen):
            SearchTab(presenter: screen.presenter)
          case let .watchlist(screen):
            WatchlistTab(presenter: screen.presenter)
          case let .settings(screen):
            SettingsTab(presenter: screen.presenter)
          }
        }
      }
    }
    .appTheme()
    .onChange(of: selectedTab) { newTab in
      switch newTab {
      case .discover: presenter.onDiscoverClicked()
      case .search: presenter.onSearchClicked()
      case .watchlist: presenter.onLibraryClicked()
      case .settings: presenter.onSettingsClicked()
      }
    }
  }

  private func tabForChild(_ child: HomePresenterChild) -> NavigationTab {
    switch onEnum(of: child) {
    case .discover: return .discover
    case .search: return .search
    case .watchlist: return .watchlist
    case .settings: return .settings
    }
  }
}

// MARK: - Tab Item

public enum NavigationTab: String, CaseIterable {
  case discover
  case search
  case watchlist
  case settings

  var title: String {
    switch self {
    case .discover: return "Discover"
    case .search: return "Search"
    case .watchlist: return "Watchlist"
    case .settings: return "Settings"
    }
  }

  var icon: String {
    switch self {
    case .discover: return "tv"
    case .search: return "magnifyingglass"
    case .watchlist: return "square.stack"
    case .settings: return "gearshape"
    }
  }
}
