//
//  FullScreenView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 07.11.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

public struct FullScreenView: View {
    private let systemName: String
    private let message: String
    private let subtitle: String?
    private let buttonText: String?
    private let color: Color
    private let action: () -> Void

    public init(
        systemName: String = "exclamationmark.triangle.fill",
        message: String = "Something went wrong",
        subtitle: String? = nil,
        buttonText: String? = nil,
        action: @escaping () -> Void = {
        },
        color: Color = .accent
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
                .foregroundColor(color)
                .font(Font.title.weight(.light))
                .frame(width: 120, height: 120)
                .padding(16)

            Text(message)
                .font(.avenirNext(size: 22))
                .fontWeight(.bold)
                .foregroundColor(.textColor)
                .multilineTextAlignment(.center)
                .padding([.horizontal], 8)

            if let subtitle {
                Text(subtitle)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }

            if let buttonText {
                FilledImageButton(
                    text: buttonText,
                    verticalPadding: 8,
                    action: action
                )
                    .background(Color.accent)
                    .cornerRadius(5)
                    .padding([.top], 4)
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
