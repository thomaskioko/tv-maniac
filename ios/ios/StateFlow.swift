//
//  StateFlow.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 4/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import TvManiac

class StateFlow<T: AnyObject>: ObservableObject {
    @Published
    var value: T?

    private var cancelable: Cancelable? = nil

    init(_ state: Kotlinx_coroutines_coreStateFlow) {
        self.value = state.value as? T

        cancelable = FlowWrapper<T>(flow: state).collect(consumer: { value in
            self.value = value
        })
    }

    deinit {
        self.cancelable?.cancel()
    }
}
