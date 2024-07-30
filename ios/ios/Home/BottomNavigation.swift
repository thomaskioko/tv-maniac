//
//  BottomNavigation.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/25/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct BottomNavigation: View {
    let screen: HomeComponentChild
    let actions: [BottomTabAction]

    var body: some View {
        VStack(spacing: 0) {
            Divider()
                .background(Color.gray.opacity(0.3))

            HStack(spacing: 0) {
                ForEach(actions) { action in
                    BottomTabView(
                        title: action.title,
                        systemImage: action.systemImage,
                        isActive: action.isActive(screen),
                        action: action.action
                    )
                }
            }
            .frame(height: 54)
            .padding(.bottom, 28)
            .padding(.top, 2)
        }
        .background(TransparentBlurView(style: .systemThinMaterial))
        .edgesIgnoringSafeArea(.bottom)
    }
}

struct BottomTabAction: Identifiable {
    let id = UUID()
    let title: String
    let systemImage: String
    let isActive: (HomeComponentChild) -> Bool
    let action: () -> Void
}
