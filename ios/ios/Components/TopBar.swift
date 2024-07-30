//
//  TobBar.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct TopBar: View {

    var progress: CGFloat
    var title: String? = nil
    var isRefreshing: Bool
    var onBackClicked: () -> Void
    var onRefreshClicked: () -> Void
    var width: CGFloat = 40
    var height: CGFloat = 40

    @Environment(\.colorScheme) var colorScheme
    @State private var isBackButtonPressed = false
    @State private var isRefreshButtonPressed = false
    @State private var rotation: Angle = .degrees(0)

    var body: some View {
        VStack{
            HStack {
                // Back Button
                CircularButton(
                    iconName: "arrow.backward",
                    action: onBackClicked,
                    width: width,
                    height: height
                )
                .padding(.leading, 16)

                if let title = title {
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
                    action: onRefreshClicked,
                    width: width,
                    height: height
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

struct TopBar_Previews: PreviewProvider {
    static var previews: some View {
        ForEach(ColorScheme.allCases, id: \.self) { colorScheme in
            TopBar(
                progress: 0,
                title: "Movie Title",
                isRefreshing: true,
                onBackClicked: {},
                onRefreshClicked: {}
            )
            .preferredColorScheme(colorScheme)
            .previewDisplayName("\(colorScheme == .light ? "Light Mode" : "Dark Mode")")
        }
    }
}
