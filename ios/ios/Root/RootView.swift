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

    @ObservedObject @StateFlow private var stack: ChildStack<AnyObject, Screen>
    @ObservedObject @StateFlow private var uiState: ThemeState
    private let navigator: Navigator

    init(navigator: Navigator) {
        self.navigator = navigator
        self._stack = .init(navigator.screenStackFlow)
        self._uiState = .init(navigator.themeState)
    }



    var body: some View {
        ZStack(alignment: Alignment(horizontal: .center, vertical: .bottom)){
            let screen = stack.active.instance

            let showBottomBar = navigator.shouldShowBottomNav(screen: screen)

            ChildView(screen: screen)
                .frame(maxHeight: .infinity)
                .padding(.bottom, showBottomBar ? 64 : 0)
                .background(Color.background)

            BottomNavigation(screen, navigator)
                .background(.ultraThinMaterial)
                .hidden(showBottomBar)
                .transition(.asymmetric(insertion: .slide, removal: .scale))
        }
        .preferredColorScheme(uiState.appTheme == AppTheme.lightTheme ? .light : uiState.appTheme == AppTheme.darkTheme ? .dark : nil)
    }
}

fileprivate func BottomNavigation(_ screen: Screen,_ rootPresenter: Navigator) -> some View {
    return HStack(alignment: .bottom, spacing: 16) {
        Spacer()

        BottomTabView(
            title: "Discover",
            systemImage: "film",
            isActive: screen is ScreenDiscover,
            action: { rootPresenter.bringToFront(config: ConfigDiscover()) }
        )

        Spacer()

        BottomTabView(
            title: "Search",
            systemImage: "magnifyingglass",
            isActive: screen is ScreenSearch,
            action: { rootPresenter.bringToFront(config: ConfigSearch()) }
        )

        Spacer()
        BottomTabView(
            title: "Library",
            systemImage: "list.bullet.below.rectangle",
            isActive: screen is ScreenLibrary,
            action: { rootPresenter.bringToFront(config: ConfigLibrary()) }
        )
        Spacer()

        BottomTabView(
            title: "Settings",
            systemImage: "gearshape",
            isActive: screen is ScreenSettings,
            action: { rootPresenter.bringToFront(config: ConfigSettings()) }
        )
        Spacer()

    }.frame(width: UIScreen.main.bounds.width, height: 64)
}

private struct ChildView: View {
    let screen: Screen

    var body: some View {
        switch onEnum(of: screen) {
            case .discover(let screen) : DiscoverView(presenter: screen.presenter)
            case .search(let screen) : SearchView(presenter: screen.presenter)
            case .library(let screen) : LibraryView(presenter: screen.presenter)
            case .settings(let screen) : SettingsView(presenter: screen.presenter)
            case .showDetails(let screen): ShowDetailView(presenter: screen.presenter)
            case .seasonDetails(let screen): SeasonDetailsView(presenter: screen.presenter)
            case .moreShows(let screen): MoreShowsView(presenter: screen.presenter)
        default:  fatalError("Unhandled Screen: \(screen)")
        }
    }
}

private struct BottomTabView: View {
    let title: String
    let systemImage: String
    let isActive: Bool
    let action: () -> Void

    var body: some View {

        Button(action: action) {
            VStack(alignment: .center) {
                Image(systemName: systemImage)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .foregroundColor(isActive ? .blue : .text_color_bg)
                    .font(Font.title.weight(.thin))
                    .frame(width: 26, height: 26)
                    .animation(.default)
                    .opacity(isActive ? 1 : 0.5)

                Spacer().frame(height: 4)

                Text(title)
                    .foregroundColor(isActive ? .blue  : .text_color_bg)
                    .bodyMediumFont(size: 14)
                    .fontWeight(.medium)
            }
        }
        .buttonStyle(.plain)
    }
}
