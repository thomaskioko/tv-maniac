//
//  ChildView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/22/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct ChildView: View {
    let screen: HomeComponentChild?
    let bottomTabActions: [BottomTabAction]
    
    var body: some View {
        VStack {
            switch onEnum(of: screen) {
                case .discover(let screen):
                    DiscoverView(component: screen.component)
                case .search(let screen):
                    SearchView(component: screen.component)
                case .library(let screen):
                    LibraryView(component: screen.component)
                case .settings(let screen):
                    SettingsView(component: screen.component)
                default:
                    fatalError("Unhandled Screen: \(String(describing: screen))")
            }
            
            Spacer()
            
            BottomNavigation(
                screen: screen!,
                actions: bottomTabActions
            )
            .background(.ultraThinMaterial)
            .transition(.asymmetric(insertion: .slide, removal: .scale))
        }
        .background(Color.background)
        
    }
}

struct BottomTabAction: Identifiable {
    let id = UUID()
    let title: String
    let systemImage: String
    let isActive: (HomeComponentChild) -> Bool
    let action: () -> Void
}

struct BottomNavigation: View {
    let screen: HomeComponentChild
    let actions: [BottomTabAction]
    
    var body: some View {
        HStack(spacing: 16) {
            Spacer()
            ForEach(actions) { action in
                BottomTabView(
                    title: action.title,
                    systemImage: action.systemImage,
                    isActive: action.isActive(screen),
                    action: action.action
                )
                Spacer()
            }
        }
        .frame(height: 64)
    }
}

