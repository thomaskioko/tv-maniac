//
//  ObservableStateFlow.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/13/24.
//

import SwiftUI
import TvManiac

public class ObservableStateFlow<T: AnyObject>: ObservableObject {
  private let stateFlow: SkieSwiftStateFlow<T>

  @Published var wrappedValue: T

  private var publisher: Task<Void, Never>?

  public init(_ stateFlow: SkieSwiftStateFlow<T>) {
    self.stateFlow = stateFlow
    self.wrappedValue = stateFlow.value

    self.publisher = Task { @MainActor [weak self] in
      if let stateFlow = self?.stateFlow {
        for await item in stateFlow {
          self?.wrappedValue = item
        }
      }
    }
  }

  deinit {
    if let publisher {
      publisher.cancel()
    }
  }
}
