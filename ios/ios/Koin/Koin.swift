//
//  KoinUtil.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 05.11.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import TvManiac
import Foundation

extension KoinApplication {
    static let shared = companion.start()

    @discardableResult
    static func start() -> KoinApplication {
        shared
    }
}


extension KoinApplication {
    private static let keyPaths: [PartialKeyPath<Koin>] = [
        \.showStateMachine,
        \.showDetailsStateMachine,
        \.settingsStateMachine
    ]

    static func inject<T>() -> T {
        shared.inject()
    }

    func inject<T>() -> T {
        for partialKeyPath in Self.keyPaths {
            guard let keyPath = partialKeyPath as? KeyPath<Koin, T> else {
                continue
            }
            return koin[keyPath: keyPath]
        }

        fatalError("\(T.self) is not registered with KoinApplication")
    }
}

@propertyWrapper
struct LazyKoin<T> {
    lazy var wrappedValue: T = {
        KoinApplication.shared.inject()
    }()

    init() {
    }
}
