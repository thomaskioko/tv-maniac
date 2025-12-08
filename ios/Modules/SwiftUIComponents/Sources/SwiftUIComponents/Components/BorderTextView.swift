//
//  BorderTextView.swift
//
//
//  Created by Thomas Kioko on 9/8/24.
//

import SwiftUI

public struct BorderTextView: View {
    @Theme private var theme

    private let text: String
    private let color: Color?
    private let colorOpacity: CGFloat
    private let borderOpacity: CGFloat
    private let borderWidth: CGFloat
    private let cornerRadius: CGFloat?
    private let weight: Font.Weight

    public init(
        text: String,
        color: Color? = nil,
        colorOpacity: CGFloat = 0,
        borderOpacity: CGFloat = 1,
        borderWidth: CGFloat = 1,
        cornerRadius: CGFloat? = nil,
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
        let resolvedColor = color ?? theme.colors.accent
        let resolvedCornerRadius = cornerRadius ?? theme.shapes.small

        VStack {
            Text(text)
                .padding(theme.spacing.xxSmall)
                .textStyle(theme.typography.labelMedium)
                .fontWeight(weight)
                .foregroundColor(resolvedColor)
                .background(
                    RoundedRectangle(cornerRadius: resolvedCornerRadius)
                        .fill(resolvedColor.opacity(colorOpacity))
                )
                .overlay(
                    RoundedRectangle(cornerRadius: resolvedCornerRadius)
                        .stroke(resolvedColor.opacity(borderOpacity), lineWidth: borderWidth)
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
