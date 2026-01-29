//
//  CenteredFullScreenView.swift
//  SwiftUIComponents
//
//  Created by Thomas Kioko on 11/5/24.
//

import SwiftUI

public struct CenteredFullScreenView<Content: View>: View {
    @Environment(\.keyboardHeight) var keyboardHeight
    private let content: () -> Content

    public init(@ViewBuilder content: @escaping () -> Content) {
        self.content = content
    }

    public var body: some View {
        GeometryReader { geometry in
            content()
                .frame(width: geometry.size.width)
                .frame(minHeight: geometry.size.height)
                .position(
                    x: geometry.size.width / 2,
                    y: keyboardHeight > 0
                        ? geometry.size.height / 3
                        : geometry.size.height / 2
                )
                .animation(.easeOut(duration: 0.25), value: keyboardHeight)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
