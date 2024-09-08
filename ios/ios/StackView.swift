//
//  StackView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/22/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import UIKit
import TvManiac

struct StackView<T: AnyObject, Content: View>: View {
    @StateFlow var stack: ChildStack<AnyObject, T>
    let onBack: (Int32) -> Void
    let content: (T) -> Content
    
    @State private var dragOffset: CGFloat = 0
    private let edgeWidth: CGFloat = 20
    private let dragThreshold: CGFloat = 0.3
    
    var body: some View {
        ZStack {
            ForEach(Array(stack.items.enumerated().reversed()), id: \.offset) { index, item in
                content(item.instance)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .modifier(NavigationViewModifier(
                        index: index,
                        currentIndex: stack.items.count - 1,
                        dragOffset: dragOffset
                    ))
            }
        }
        .gesture(makeNavigationGesture())
    }
    
    private func makeNavigationGesture() -> some Gesture {
        DragGesture(minimumDistance: 10, coordinateSpace: .global)
            .onChanged { value in
                guard value.startLocation.x <= edgeWidth, stack.items.count > 1 else { return }
                dragOffset = min(max(0, value.translation.width), UIScreen.main.bounds.width)
            }
            .onEnded { value in
                guard value.startLocation.x <= edgeWidth, stack.items.count > 1 else { return }
                let shouldPop = value.translation.width > UIScreen.main.bounds.width * dragThreshold
                if shouldPop {
                    onBack(Int32(stack.items.count - 2))
                }
                dragOffset = 0
            }
    }
}
