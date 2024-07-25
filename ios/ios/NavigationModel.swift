//
//  NavigationModel.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/25/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

class NavigationModel: ObservableObject {
    @Published private(set) var viewStack: [RootComponentChild] = []
    @Published private(set) var currentIndex: Int = 0
    @Published var dragOffset: CGFloat = 0

    func setStack(_ stack: ChildStack<AnyObject, RootComponentChild>) {
        viewStack = stack.items.compactMap { $0.instance }
        currentIndex = viewStack.count - 1
    }

    func popView() {
        guard currentIndex > 0 else { return }
        currentIndex -= 1
    }
}

