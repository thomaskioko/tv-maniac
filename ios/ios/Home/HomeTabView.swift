//
//  TabView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/23/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac


struct HomeTabView: View {
    private let component: HomeComponent
    @StateFlow private var stack: ChildStack<AnyObject, HomeComponentChild>
    private var activeChild: HomeComponentChild { stack.active.instance }
    
    init(component: HomeComponent) {
        self.component = component
        _stack = StateFlow(component.stack)
    }
    
    var body: some View {
        VStack{
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
                isActive: { $0 is HomeComponentChildDiscover },
                action: { component.onDiscoverClicked() }
            ),
            BottomTabAction(
                title: "Search",
                systemImage: "magnifyingglass",
                isActive: { $0 is HomeComponentChildSearch },
                action: { component.onSearchClicked() }
            ),
            BottomTabAction(
                title: "Library",
                systemImage: "list.bullet.below.rectangle",
                isActive: { $0 is HomeComponentChildLibrary },
                action: { component.onLibraryClicked() }
            ),
            BottomTabAction(
                title: "Settings",
                systemImage: "gearshape",
                isActive: { $0 is HomeComponentChildSettings },
                action: { component.onSettingsClicked() }
            )
        ]
    }
}
