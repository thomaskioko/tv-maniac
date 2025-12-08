//
//  TransparentGroupBox.swift
//
//
//  Created by Thomas Kioko on 9/8/24.
//

import SwiftUI

struct TransparentGroupBox: GroupBoxStyle {
    @Theme private var theme

    func makeBody(configuration: Configuration) -> some View {
        VStack {
            HStack {
                configuration.label
                    .textStyle(theme.typography.titleSmall)
                    .foregroundColor(theme.colors.onSurface)
                Spacer()
            }

            configuration.content
                .foregroundColor(theme.colors.onSurface)
        }
        .padding()
        .background {
            ZStack {
                Rectangle().fill(theme.colors.background)
            }
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium, style: .continuous))
            .shadow(radius: 1)
        }
    }
}
