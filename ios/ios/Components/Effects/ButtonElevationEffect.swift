//
//  ButtonElevationEffect.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/11/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct ButtonElevationEffect: ViewModifier {
    @Binding var isPressed: Bool

    func body(content: Content) -> some View {
        content
            .scaleEffect(isPressed ? 0.9 : 1.0)
            .opacity(isPressed ? 0.8 : 1.0)
            .animation(.easeInOut(duration: 0.2), value: isPressed)
    }
}

extension View {
    func buttonElevationEffect(isPressed: Binding<Bool>) -> some View {
        self.modifier(ButtonElevationEffect(isPressed: isPressed))
    }
}
