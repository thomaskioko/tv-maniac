//
//  AsyncSequence.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 4/6/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

public extension AsyncSequence {
    func collect(_ block: (Element) async throws -> Void) async rethrows {
        for try await element in self {
            try await block(element)
        }
    }

    func collect() async rethrows -> [Element] {
        var elements = [Element]()
        for try await element in self {
            elements.append(element)
        }
        return elements
    }
}
