//
//  RootHolder.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 03.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import TvManiac

class RootHolder : ObservableObject {
    let lifecycle: LifecycleRegistry
    
    init() {
        lifecycle = LifecycleRegistryKt.LifecycleRegistry()
        LifecycleRegistryExtKt.create(lifecycle)
    }
    
    deinit {
        // Destroy the root component before it is deallocated
        LifecycleRegistryExtKt.destroy(lifecycle)
    }
}
