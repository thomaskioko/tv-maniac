//
//  StateFlow.swift
//  TvManiacKit
//
//  Created by Thomas Kioko on 11/13/24.
//

import Foundation
import SwiftUI
import TvManiac

@propertyWrapper struct StateFlow<T: AnyObject>: DynamicProperty {
    @StateObject private var observable: ObservableStateFlow<T>

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
        observer.observe { [weak self] newValue in
            self?.value = newValue!
        }
    }

    deinit {
        observer.unsubscribe()
    }
}
