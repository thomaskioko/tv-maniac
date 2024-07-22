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

@propertyWrapper
struct StateFlow<T: AnyObject>: DynamicProperty {
    @StateObject
    private var state: StateFlowObject<T>

    var wrappedValue: T? {
        state.value
    }

    var projectedValue: StateFlowObject<T> {
        state
    }

    init(_ stateFlow: Kotlinx_coroutines_coreStateFlow) {
        _state = StateObject(wrappedValue: StateFlowObject(stateFlow))
    }
}

class StateFlowObject<T: AnyObject>: ObservableObject {
    @Published
    var value: T?

    private var cancelable: Cancelable? = nil

    init(_ state: Kotlinx_coroutines_coreStateFlow) {
        self.value = state.value as? T

        cancelable = FlowWrapper<T>(flow: state).collect(consumer: { [weak self] value in
            self?.value = value
        })
    }

    deinit {
        cancelable?.cancel()
    }
}
