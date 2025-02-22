//
//  Test.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/8/24.
//

import SwiftUI
import TvManiac
import UIKit

public struct DecomposeNavigationStack<T: AnyObject, Content: View>: View {
  // MARK: - Properties
  @StateFlow var childStack: ChildStack<AnyObject, T>
  @ViewBuilder private let content: (T) -> Content
  private let getTitle: (T) -> String
  private let onBack: (Int32) -> Void

  private var stack: [Child<AnyObject, T>] { childStack.items }

  // MARK: - Initializer
  public init(
    childStack: SkieSwiftStateFlow<ChildStack<AnyObject, T>>,
    getTitle: @escaping (T) -> String = { _ in "" },
    onBack: @escaping (Int32) -> Void,
    @ViewBuilder content: @escaping (T) -> Content
  ) {
    self._childStack = .init(childStack)
    self.getTitle = getTitle
    self.onBack = onBack
    self.content = content
  }

  // MARK: - Body
  public var body: some View {
    NavigationStack(
      path: Binding(
        get: { stack.dropFirst() },
        set: { updatedPath in onBack(Int32(updatedPath.count)) }
      )
    ) {
      content(stack.first!.instance!)
        .navigationDestination(for: Child<AnyObject, T>.self) {
          content($0.instance!)
        }
    }
  }
}

