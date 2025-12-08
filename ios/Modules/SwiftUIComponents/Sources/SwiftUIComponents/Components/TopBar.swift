//
//  TopBar.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

public struct TopBar: View {
    @Theme private var theme
    @Environment(\.colorScheme) private var colorScheme
    @State private var isBackButtonPressed = false
    @State private var isRefreshButtonPressed = false
    @State private var rotation: Angle = .degrees(0)

    private let progress: CGFloat
    private let title: String?
    private let isRefreshing: Bool
    private let onBackClicked: () -> Void
    private let onRefreshClicked: () -> Void
    private let width: CGFloat
    private let height: CGFloat

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
                CircularButton(
                    iconName: "arrow.backward",
                    width: width,
                    height: height,
                    action: onBackClicked
                )
                .padding(.leading, theme.spacing.medium)

                if let title {
                    Text(title)
                        .textStyle(theme.typography.headlineSmall)
                        .lineLimit(1)
                        .padding(.leading, theme.spacing.xxSmall)
                        .opacity(progress)
                        .opacity(max(0, min(1, (progress - 0.75) * 4.0)))
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.bottom, theme.spacing.xxSmall)
                } else {
                    Spacer()
                }

                CircularButton(
                    iconName: "arrow.clockwise",
                    width: width,
                    height: height,
                    action: onRefreshClicked
                )
                .rotationEffect(rotation)
                .padding(.trailing, theme.spacing.medium)
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
