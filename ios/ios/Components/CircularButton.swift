//
//  CircularButton.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/14/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct CircularButton: View {
    let iconName: String
    let action: () -> Void
    let width: CGFloat
    let height: CGFloat

    @Environment(\.colorScheme) var colorScheme
    @State private var isPressed = false

    var body: some View {
        Button(action: {
            isPressed = true
            action()
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                isPressed = false
            }
        }) {
            ZStack {
                Circle()
                    .fill(colorScheme == .dark ? Color.white : .gray.opacity(0.8))
                    .overlay(
                        Image(systemName: iconName)
                            .resizable()
                            .scaledToFit()
                            .foregroundColor(colorScheme == .dark ? Color.black : Color.white)
                            .font(.system(size: 20, weight: .bold))
                            .padding(12)
                    )
                    .frame(width: width, height: height)
                    .buttonElevationEffect(isPressed: $isPressed)
            }
        }
        .buttonStyle(PlainButtonStyle())
    }
}
