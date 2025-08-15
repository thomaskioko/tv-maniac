//
//  ThemedProgressView.swift
//  SwiftUIComponents
//
//  Created by Thomas Kioko on 8/10/25.
//

import SwiftUI

public struct ThemedProgressView: View {
    @State private var rotationAngle: Double = 0

    public init() {}

    public var body: some View {
        ZStack {
            Capsule()
                .fill(.ultraThinMaterial)
                .frame(width: 40, height: 40)
                .shadow(color: .black.opacity(0.2), radius: 4, y: 2)

            ZStack {
                Circle()
                    .stroke(Color.accent.opacity(0.2), lineWidth: 3)
                    .frame(width: 24, height: 24)

                Circle()
                    .trim(from: 0, to: 0.7)
                    .stroke(
                        LinearGradient(
                            colors: [Color.accent, Color.accent.opacity(0.6)],
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
    HStack(spacing: 10) {
        ThemedProgressView()
    }
}
