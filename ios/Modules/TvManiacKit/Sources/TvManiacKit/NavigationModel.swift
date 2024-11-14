//
//  NavigationModel.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/13/24.
//

import Foundation
import TvManiac

public class NavigationModel: ObservableObject {
  @Published private(set) var viewStack: [RootPresenterChild]
  @Published private(set) var currentIndex: Int
  @Published var dragOffset: CGFloat

  public init(
    viewStack: [RootPresenterChild] = [],
    currentIndex: Int = 0,
    dragOffset: CGFloat = 0
  ) {
    self.viewStack = viewStack
    self.currentIndex = currentIndex
    self.dragOffset = dragOffset
  }

  func setStack(_ stack: ChildStack<AnyObject, RootPresenterChild>) {
    viewStack = stack.items.compactMap { $0.instance }
    currentIndex = viewStack.count - 1
  }

  func popView() {
    guard currentIndex > 0 else { return }
    currentIndex -= 1
  }
}
