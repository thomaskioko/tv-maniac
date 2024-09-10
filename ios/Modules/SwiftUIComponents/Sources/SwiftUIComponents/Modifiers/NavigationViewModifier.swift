//
//  NavigationViewModifier.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/25/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

public struct NavigationViewModifier: ViewModifier {
    private let index: Int
    private let currentIndex: Int
    private let dragOffset: CGFloat

    public init(index: Int, currentIndex: Int, dragOffset: CGFloat) {
        self.index = index
        self.currentIndex = currentIndex
        self.dragOffset = dragOffset
    }

    public func body(content: Content) -> some View {
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
