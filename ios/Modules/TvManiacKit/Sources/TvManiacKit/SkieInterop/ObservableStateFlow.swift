//
//  ObservableStateFlow.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/13/24.
//

import SwiftUI
import TvManiac

public class ObservableStateFlow<T: AnyObject>: ObservableObject {
  @Published var wrappedValue: T
  @Published private(set) var error: Error?
  private let stateFlow: SkieSwiftStateFlow<T>
  private var publisher: Task<Void, Never>?

  public init(_ stateFlow: SkieSwiftStateFlow<T>) {
    self.stateFlow = stateFlow
    self.wrappedValue = stateFlow.value

    setupPublisher()
  }

  private func setupPublisher() {
    publisher = Task { @MainActor [weak self] in
      guard let stateFlow = self?.stateFlow else { return }
      for await item in stateFlow {
        self?.wrappedValue = item
        self?.error = nil
      }
    }
  }

  deinit {
    if let publisher {
      publisher.cancel()
    }
  }
}
