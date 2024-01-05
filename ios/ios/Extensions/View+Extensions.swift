//
//  View+Extensions.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 04.01.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

extension View {
    func hidden(_ showBottomBar: Bool) -> some View {
        withAnimation {
            opacity(showBottomBar ? 1 : 0)
        }
    }
}
