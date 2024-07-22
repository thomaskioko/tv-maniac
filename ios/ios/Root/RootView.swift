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
    private let navigator: Navigator
    @StateFlow private var stack: ChildStack<AnyObject, Screen>?
    @StateFlow private var uiState: ThemeState?

    init(navigator: Navigator) {
        self.navigator = navigator
        _stack = StateFlow(navigator.screenStackFlow)
        _uiState = StateFlow(navigator.themeState)
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            let screen = stack?.active.instance

            ChildView(screen: screen)
                .frame(maxHeight: .infinity)
                .background(Color.background)

            if let newScreen = screen {

                let showBottomBar = navigator.shouldShowBottomNav(screen: newScreen)
                BottomNavigation(screen: newScreen, navigator: navigator)
                    .background(.ultraThinMaterial)
                    .hidden(showBottomBar)
                    .transition(.asymmetric(insertion: .slide, removal: .scale))

            }

        }
        .animation(.easeInOut, value: stack)
        .environment(\.colorScheme, colorScheme ?? .dark)
    }

    private var colorScheme: ColorScheme? {
        switch uiState?.appTheme {
            case .lightTheme: return .light
            case .darkTheme: return .dark
            default: return nil
        }
    }
}

struct BottomNavigation: View {
    let screen: Screen
    let navigator: Navigator

    var body: some View {
        HStack(spacing: 16) {
            Spacer()
            BottomTabView(title: "Discover", systemImage: "film", isActive: screen is ScreenDiscover) {
                navigator.bringToFront(config: ConfigDiscover())
            }
            Spacer()
            BottomTabView(title: "Search", systemImage: "magnifyingglass", isActive: screen is ScreenSearch) {
                navigator.bringToFront(config: ConfigSearch())
            }
            Spacer()
            BottomTabView(title: "Library", systemImage: "list.bullet.below.rectangle", isActive: screen is ScreenLibrary) {
                navigator.bringToFront(config: ConfigLibrary())
            }
            Spacer()
            BottomTabView(title: "Settings", systemImage: "gearshape", isActive: screen is ScreenSettings) {
                navigator.bringToFront(config: ConfigSettings())
            }
            Spacer()
        }
        .frame(height: 64)
    }
}

struct ChildView: View {
    let screen: Screen?

    var body: some View {
        Group {
            switch onEnum(of: screen) {
                case .discover(let screen):
                    DiscoverView(presenter: screen.presenter)
                case .search(let screen):
                    SearchView(presenter: screen.presenter)
                case .library(let screen):
                    LibraryView(presenter: screen.presenter)
                case .settings(let screen):
                    SettingsView(presenter: screen.presenter)
                case .showDetails(let screen):
                    ShowDetailView(presenter: screen.presenter)
                case .seasonDetails(let screen):
                    SeasonDetailsView(presenter: screen.presenter)
                case .moreShows(let screen):
                    MoreShowsView(presenter: screen.presenter)
                default:
                    fatalError("Unhandled Screen: \(String(describing: screen))")
            }
        }
    }
}
