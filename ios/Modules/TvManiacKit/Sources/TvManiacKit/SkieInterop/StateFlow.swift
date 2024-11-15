//
//  StateFlow.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/13/24.
//

import SwiftUI
import TvManiac

@propertyWrapper
public struct StateFlow<T: AnyObject>: DynamicProperty {
  @StateObject private var observable: ObservableStateFlow<T>

  public init(_ stateFlow: SkieSwiftStateFlow<T>) {
    _observable = StateObject(wrappedValue: ObservableStateFlow(stateFlow))
  }

  public var wrappedValue: T { observable.wrappedValue }
  public var projectedValue: ObservableStateFlow<T> { observable }
}
