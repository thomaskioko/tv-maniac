import SwiftUI
import TvManiacKit

public struct TabBarView: View {
    private let presenter: HomePresenter
    @StateObject @KotlinStateFlow private var stack: ChildStack<AnyObject, HomePresenterChild>
    @State private var selectedTab: NavigationTab = .discover
    @EnvironmentObject private var appDelegate: AppDelegate

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
                            .id(ObjectIdentifier(screen))
                    case let .search(screen):
                        SearchTab(presenter: screen.presenter)
                            .id(ObjectIdentifier(screen))
                    case let .watchlist(screen):
                        WatchlistTab(presenter: screen.presenter)
                            .id(ObjectIdentifier(screen))
                    case let .settings(screen):
                        SettingsTab(
                            presenter: screen.presenter,
                            authRepository: appDelegate.traktAuthRepository
                        )
                        .id(ObjectIdentifier(screen))
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
        case .discover: .discover
        case .search: .search
        case .watchlist: .watchlist
        case .settings: .settings
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
        case .discover: String(\.label_tab_discover)
        case .search: String(\.label_tab_search)
        case .watchlist: String(\.label_tab_watchlist)
        case .settings: String(\.label_tab_settings)
        }
    }

    var icon: String {
        switch self {
        case .discover: "tv"
        case .search: "magnifyingglass"
        case .watchlist: "square.stack"
        case .settings: "gearshape"
        }
    }
}
