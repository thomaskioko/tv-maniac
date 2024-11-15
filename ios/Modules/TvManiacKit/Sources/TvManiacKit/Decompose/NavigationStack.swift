//
//  Test.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/8/24.
//

import SwiftUI
import SwiftUIComponents
import TvManiac
import UIKit

public struct NavigationStack<T: AnyObject, Content: View>: View {
  @StateFlow var stack: ChildStack<AnyObject, T>
  @State private var dragOffset: CGFloat = 0
  @ViewBuilder private let content: (T) -> Content

  private let edgeWidth: CGFloat
  private let dragThreshold: CGFloat
  private let onBack: (Int32) -> Void

  public init(
    stack: SkieSwiftStateFlow<ChildStack<AnyObject, T>>,
    dragOffset: CGFloat = 0,
    dragThreshold: CGFloat = 0.3,
    edgeWidth: CGFloat = 20,
    onBack: @escaping (Int32) -> Void,
    @ViewBuilder content: @escaping (T) -> Content
  ) {
    self._stack = .init(stack)
    self.onBack = onBack
    self.content = content
    self.dragOffset = dragOffset
    self.dragThreshold = dragThreshold
    self.edgeWidth = edgeWidth
  }

  public var body: some View {
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
