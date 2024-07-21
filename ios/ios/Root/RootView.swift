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
    @ObservedObject private var stack: StateFlow<ChildStack<AnyObject, Screen>>
    @ObservedObject private var uiState: StateFlow<ThemeState>
    private let navigator: Navigator
    
    init(navigator: Navigator) {
        self.navigator = navigator
        self.stack = StateFlow(navigator.screenStackFlow)
        self.uiState = StateFlow(navigator.themeState)
    }
    
    var body: some View {
        ZStack(alignment: .bottom) {
            let screen = stack.value?.active.instance
            
            ChildView(screen: screen)
                .frame(maxHeight: .infinity)
                .background(Color.background)

            if let screen = screen, navigator.shouldShowBottomNav(screen: screen) {
                BottomNavigation(screen: screen, navigator: navigator)
                    .background(.ultraThinMaterial)
                    .transition(.asymmetric(insertion: .slide, removal: .scale))
            }
        }
        .animation(.easeInOut, value: stack.value)
        .environment(\.colorScheme, colorScheme ?? .dark)
    }
    
    private var colorScheme: ColorScheme? {
        switch uiState.value?.appTheme {
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
