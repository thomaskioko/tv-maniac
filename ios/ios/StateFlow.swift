//
//  StateFlow.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 4/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import TvManiac

@propertyWrapper struct StateFlow<T: AnyObject>: DynamicProperty {
    @StateObject var observable: ObservableStateFlow<T>

    init(_ stateFlow: Kotlinx_coroutines_coreStateFlow) {
        _observable = StateObject(wrappedValue: ObservableStateFlow(stateFlow: stateFlow))
    }

    public var wrappedValue: T { observable.value }

    public var projectedValue: ObservableStateFlow<T> { observable }
}

class ObservableStateFlow<T: AnyObject>: ObservableObject {
    @Published private(set) var value: T
    private let observer: StateFlowObserver<T>

    init(stateFlow: Kotlinx_coroutines_coreStateFlow) {
        observer = StateFlowObserver(stateFlow: stateFlow)
        value = stateFlow.value as! T
        observer.observe { newValue in
            self.value = newValue!
        }
    }

    deinit {
        observer.unsubscribe()
    }
}
