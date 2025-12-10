//
//  FullScreenView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 07.11.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

public struct FullScreenView: View {
    @Theme private var theme

    private let systemName: String
    private let message: String
    private let subtitle: String?
    private let buttonText: String?
    private let color: Color?
    private let action: () -> Void

    public init(
        systemName: String = "exclamationmark.triangle.fill",
        message: String = "Something went wrong",
        subtitle: String? = nil,
        buttonText: String? = nil,
        action: @escaping () -> Void = {},
        color: Color? = nil
    ) {
        self.systemName = systemName
        self.message = message
        self.subtitle = subtitle
        self.buttonText = buttonText
        self.action = action
        self.color = color
    }

    public var body: some View {
        VStack {
            Image(systemName: systemName)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .foregroundColor(color ?? theme.colors.accent)
                .textStyle(theme.typography.titleLarge)
                .frame(width: 120, height: 120)
                .padding(theme.spacing.medium)

            Text(message)
                .textStyle(theme.typography.titleLarge)
                .foregroundColor(theme.colors.onSurface)
                .multilineTextAlignment(.center)
                .padding([.horizontal], theme.spacing.xSmall)

            if let subtitle {
                Text(subtitle)
                    .textStyle(theme.typography.bodySmall)
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }

            if let buttonText {
                FilledImageButton(
                    text: buttonText,
                    verticalPadding: theme.spacing.xSmall,
                    action: action
                )
                .background(theme.colors.accent)
                .cornerRadius(theme.shapes.small)
                .padding([.top], theme.spacing.xxSmall)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview {
    VStack {
        FullScreenView(
            systemName: "exclamationmark.triangle.fill",
            message: "Something went wrong"
        )
    }
}

#Preview {
    VStack {
        FullScreenView(
            systemName: "exclamationmark.triangle.fill",
            message: "Something went wrong",
            buttonText: "Retry"
        )
    }
}
