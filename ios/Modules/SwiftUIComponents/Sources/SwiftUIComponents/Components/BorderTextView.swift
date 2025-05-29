//
//  BorderTextView.swift
//
//
//  Created by Thomas Kioko on 9/8/24.
//

import SwiftUI

public struct BorderTextView: View {
    private let text: String
    private let color: Color
    private let colorOpacity: CGFloat
    private let borderOpacity: CGFloat
    private let borderWidth: CGFloat
    private let cornerRadius: CGFloat
    private let weight: Font.Weight

    public init(
        text: String,
        color: Color = Color.accent,
        colorOpacity: CGFloat = 0,
        borderOpacity: CGFloat = 1,
        borderWidth: CGFloat = 1,
        cornerRadius: CGFloat = 4,
        weight: Font.Weight = .light
    ) {
        self.text = text
        self.borderWidth = borderWidth
        self.color = color
        self.cornerRadius = cornerRadius
        self.borderOpacity = borderOpacity
        self.weight = weight
        self.colorOpacity = colorOpacity
    }

    public var body: some View {
        VStack {
            Text(text)
                .padding(4)
                .font(.avenirNext(size: 12))
                .fontWeight(weight)
                .foregroundColor(color)
                .background(
                    RoundedRectangle(cornerRadius: cornerRadius)
                        .fill(color.opacity(colorOpacity))
                )
                .overlay(
                    RoundedRectangle(cornerRadius: cornerRadius)
                        .stroke(color.opacity(borderOpacity), lineWidth: borderWidth)
                )
        }
    }
}

#Preview {
    HStack(spacing: 10) {
        BorderTextView(
            text: "Continuing",
            colorOpacity: 0.12,
            borderOpacity: 0.12,
            weight: .bold
        )

        BorderTextView(text: "2024")
    }
}
