//
//  ThemedProgressView.swift
//  Components
//
//  Created by Thomas Kioko on 8/10/25.
//

import DesignSystem
import SwiftUI

public struct ThemedProgressView: View {
    @Environment(\.appTheme) private var theme
    @State private var rotationAngle: Double = 0

    public init() {}

    public var body: some View {
        ZStack {
            Capsule()
                .fill(.ultraThinMaterial)
                .frame(width: 40, height: 40)
                .appShadow(theme.shadows.medium, color: theme.colors.onSurface.opacity(0.2))

            ZStack {
                Circle()
                    .stroke(.appAccent.opacity(0.2), lineWidth: 3)
                    .frame(width: 24, height: 24)

                Circle()
                    .trim(from: 0, to: 0.7)
                    .stroke(
                        LinearGradient(
                            colors: [theme.colors.accent, theme.colors.accent.opacity(0.6)],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        ),
                        style: StrokeStyle(lineWidth: 3, lineCap: .round)
                    )
                    .frame(width: 24, height: 24)
                    .rotationEffect(.degrees(rotationAngle))
                    .onAppear {
                        withAnimation(.linear(duration: 1.0).repeatForever(autoreverses: false)) {
                            rotationAngle = 360
                        }
                    }
            }
        }
    }
}

#Preview {
    HStack(spacing: TvManiacSpacingScheme.default.xSmall) {
        ThemedProgressView()
    }
}
