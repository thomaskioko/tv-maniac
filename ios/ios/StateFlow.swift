//
//  StateFlow.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 4/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

@propertyWrapper
class StateFlow<T: AnyObject>: ObservableObject {

    private let stateFlow: SkieSwiftStateFlow<T>

    @Published var wrappedValue: T

    private var publisher: Task<(), Never>?

    init(_ value: SkieSwiftStateFlow<T>) {
        self.stateFlow = value
        self.wrappedValue = value.value

        self.publisher = Task { @MainActor in
            for await item in stateFlow {
                self.wrappedValue = item
            }
        }
    }

    deinit {
        if let publisher {
            publisher.cancel()
        }
    }
}

extension ObservedObject {
    init<F>(_ stateFlow: SkieSwiftStateFlow<F>) where ObjectType == StateFlow<F> {
        self.init(wrappedValue: StateFlow(stateFlow))
    }
}
