//
//  EmptyUIView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 04.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import DesignSystem
import SwiftUI

public struct EmptyUIView: View {
    @Environment(\.appTheme) private var theme

    private let title: String
    private let subtitle: String?

    public init(
        title: String,
        subtitle: String? = nil
    ) {
        self.title = title
        self.subtitle = subtitle
    }

    public var body: some View {
        VStack {
            Spacer()

            Text("🚧")
                .textStyle(theme.typography.displayLarge)
                .padding(theme.spacing.medium)

            Text(title)
                .textStyle(theme.typography.headlineMedium)
                .frame(maxWidth: .infinity)

            if let text = subtitle {
                Text(text)
                    .textStyle(theme.typography.bodyMedium)
                    .frame(maxWidth: .infinity)
            }

            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(.horizontal, theme.spacing.medium)
    }
}

#Preview {
    EmptyUIView(
        title: "Construction In progress!!",
        subtitle: "Please wait we are wokfing on this!"
    )
}
