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
    
    let rootPresenter: RootNavigationPresenter
    
    @StateValue
    private var stack: ChildStack<AnyObject, Screen>
    
    @StateValue
    var uiState: ThemeState
    
    init(rootPresenter: RootNavigationPresenter) {
        self.rootPresenter = rootPresenter
        _stack = StateValue(rootPresenter.screenStack)
        _uiState = StateValue(rootPresenter.state)
    }
    
    
    
    var body: some View {
        ZStack(alignment: Alignment(horizontal: .center, vertical: .bottom)){
            let screen = stack.active.instance
            
            ChildView(screen: screen)
                .frame(maxHeight: .infinity)
            
            //TODO:: Animate visibility based on the screen. Only show when it's a tab screen
            BottomNavigation(screen, rootPresenter)
                .background(.ultraThinMaterial)
            
        }
        .preferredColorScheme(uiState.appTheme == AppTheme.lightTheme ? .light : uiState.appTheme == AppTheme.darkTheme ? .dark : nil)
    }
}

fileprivate func BottomNavigation(_ screen: Screen,_ rootPresenter: RootNavigationPresenter) -> some View {
    return HStack(alignment: .bottom, spacing: 16) {
        Spacer()
        
        BottomTabView(
            title: "Discover",
            systemImage: "film",
            isActive: screen is ScreenDiscover,
            action: { rootPresenter.bringToFront(config: RootNavigationPresenterConfigDiscover()) }
        )
        
        Spacer()
        
        BottomTabView(
            title: "Search",
            systemImage: "magnifyingglass",
            isActive: screen is ScreenSearch,
            action: { rootPresenter.bringToFront(config: RootNavigationPresenterConfigSearch()) }
        )
        
        Spacer()
        BottomTabView(
            title: "Library",
            systemImage: "list.bullet.below.rectangle",
            isActive: screen is ScreenLibrary,
            action: { rootPresenter.bringToFront(config: RootNavigationPresenterConfigLibrary()) }
        )
        Spacer()
        
        BottomTabView(
            title: "Profile",
            systemImage: "person.circle",
            isActive: screen is ScreenSettings,
            action: { rootPresenter.bringToFront(config: RootNavigationPresenterConfigSettings()) }
        )
        Spacer()
        
    }.frame(width: UIScreen.main.bounds.width, height: 64)
}

private struct ChildView: View {
    let screen: Screen
    
    var body: some View {
        switch screen {
        case let screen as ScreenDiscover : DiscoverView(presenter: screen.presenter)
        case let screen as ScreenSearch : SearchView(presenter: screen.presenter)
        case let screen as ScreenLibrary : LibraryView(presenter: screen.presenter)
        case let screen as ScreenSettings : SettingsUIView(presenter: screen.presenter)
        case let screen as ScreenShowDetails: ShowDetailView(presenter: screen.presenter)
        default: EmptyView()
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
                    .foregroundColor(isActive ? Color.accent : Color.gray)
                    .font(Font.title.weight(.light))
                    .frame(width: 28, height: 28)
                    .animation(.default)
                    .opacity(isActive ? 1 : 0.5)
                
                Spacer().frame(height: 4)
                
                Text(title)
                    .foregroundColor(isActive ? Color.accent : .gray)
                    .bodyMediumFont(size: 16)
            }
            
        }
    }
}
