//
//  TransparentGroupBox.swift
//
//
//  Created by Thomas Kioko on 9/8/24.
//

import DesignSystem
import SwiftUI

struct TransparentGroupBox: GroupBoxStyle {
    @Environment(\.appTheme) private var theme

    func makeBody(configuration: Configuration) -> some View {
        VStack {
            HStack {
                configuration.label
                    .textStyle(theme.typography.titleSmall)
                    .foregroundStyle(.appOnSurface)
                Spacer()
            }

            configuration.content
                .foregroundStyle(.appOnSurface)
        }
        .padding()
        .background {
            ZStack {
                Rectangle().fill(.appBackground)
            }
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium, style: .continuous))
            .shadow(radius: 1)
        }
    }
}
