//
//  TopBar.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct TopBar: View {
    private let progress: CGFloat
    private let title: String?
    private let isRefreshing: Bool
    private let onBackClicked: () -> Void
    private let onRefreshClicked: () -> Void
    private let width: CGFloat
    private let height: CGFloat

    @Environment(\.colorScheme) private var colorScheme
    @State private var isBackButtonPressed = false
    @State private var isRefreshButtonPressed = false
    @State private var rotation: Angle = .degrees(0)

    public init(
        progress: CGFloat,
        title: String? = nil,
        isRefreshing: Bool,
        onBackClicked: @escaping () -> Void,
        onRefreshClicked: @escaping () -> Void,
        width: CGFloat = 40,
        height: CGFloat = 40,
        isBackButtonPressed: Bool = false,
        isRefreshButtonPressed: Bool = false,
        rotation: Angle = .degrees(0)
    ) {
        self.progress = progress
        self.title = title
        self.isRefreshing = isRefreshing
        self.onBackClicked = onBackClicked
        self.onRefreshClicked = onRefreshClicked
        self.width = width
        self.height = height
        self.isBackButtonPressed = isBackButtonPressed
        self.isRefreshButtonPressed = isRefreshButtonPressed
        self.rotation = rotation
    }

    public var body: some View {
        VStack {
            HStack {
                // Back Button
                CircularButton(
                    iconName: "arrow.backward",
                    width: width,
                    height: height,
                    action: onBackClicked
                )
                    .padding(.leading, 16)

                if let title {
                    Text(title)
                        .bodyFont(size: 24)
                        .fontWeight(.semibold)
                        .lineLimit(1)
                        .padding(.leading, 4)
                        .opacity(progress)
                        .opacity(max(0, min(1, (progress - 0.75) * 4.0)))
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.bottom, 4)
                } else {
                    Spacer()
                }

                // Refresh Button
                CircularButton(
                    iconName: "arrow.clockwise",
                    width: width,
                    height: height,
                    action: onRefreshClicked
                )
                    .rotationEffect(rotation)
                    .padding(.trailing, 16)
            }
            .frame(height: height)
            .padding(.top, 60)

            Spacer()
        }
        .ignoresSafeArea()
        .onChange(of: isRefreshing) { newValue in
            if newValue {
                withAnimation(Animation.linear(duration: 1).repeatForever(autoreverses: false)) {
                    rotation = .degrees(360)
                }
            } else {
                withAnimation(.linear(duration: 0.2)) {
                    rotation = .degrees(0)
                }
            }
        }
    }
}

#Preview {
    TopBar(
        progress: 0,
        title: "Movie Title",
        isRefreshing: true,
        onBackClicked: {},
        onRefreshClicked: {}
    )
}
