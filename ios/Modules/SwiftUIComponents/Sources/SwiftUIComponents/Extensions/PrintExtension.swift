//
//  PrintExtension.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 03.02.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

public extension View {
    func Print(_ vars: Any...) -> some View {
        for v in vars {
            print(v)
        }
        return EmptyView()
    }
}
