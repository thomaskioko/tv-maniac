//
//  NavigationViewModifier.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/25/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct NavigationViewModifier: ViewModifier {
    let index: Int
    let currentIndex: Int
    let dragOffset: CGFloat

    func body(content: Content) -> some View {
        content
            .offset(x: offsetForIndex())
            .zIndex(Double(index))
    }

    private func offsetForIndex() -> CGFloat {
        let baseOffset = CGFloat(index - currentIndex) * UIScreen.main.bounds.width
        if index < currentIndex {
            return -30 + (dragOffset / UIScreen.main.bounds.width) * 30
        } else if index == currentIndex {
            return dragOffset
        } else {
            return baseOffset + dragOffset
        }
    }
}
