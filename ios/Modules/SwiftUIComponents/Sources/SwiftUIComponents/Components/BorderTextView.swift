//
//  File.swift
//
//
//  Created by Thomas Kioko on 9/8/24.
//

import SwiftUI

public struct BorderTextView: View {
    private let text: String
    private let color: Color
    private let backgroundColor: Color
    private let borderColor: Color
    private let borderWidth: CGFloat
    private let cornerRadius: CGFloat
    private let weight: Font.Weight

    public init(
        text: String,
        color: Color = Color.accent,
        backgroundColor: Color = Color.clear,
        borderColor: Color = Color.accent,
        borderWidth: CGFloat = 1,
        cornerRadius: CGFloat = 4,
        weight: Font.Weight = .light
    ) {
        self.text = text
        self.borderWidth = borderWidth
        self.backgroundColor = backgroundColor
        self.color = color
        self.cornerRadius = cornerRadius
        self.borderColor = borderColor
        self.weight = weight
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
                        .fill(backgroundColor)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: cornerRadius)
                        .stroke(borderColor, lineWidth: borderWidth)
                )
        }
    }
}

#Preview {
    HStack(spacing: 10) {
        BorderTextView(
            text: "Continuing",
            backgroundColor: Color.accent.opacity(0.12),
            borderColor: Color.accent.opacity(0.12),
            weight: .bold
        )

        BorderTextView(text: "2024")
    }
}
