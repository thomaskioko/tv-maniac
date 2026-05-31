//
//  SplashView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 12/8/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Components
import DesignSystem
import SwiftUI
import SwiftUIComponents

struct SplashView: View {
    @Environment(\.appTheme) private var theme
    @State private var isActive = false
    @State private var logoScale: CGFloat = 0.6
    @State private var logoOpacity: Double = 0
    private let isDebug: Bool
    private let content: AnyView

    init(isDebug: Bool, @ViewBuilder content: @escaping () -> some View) {
        self.isDebug = isDebug
        self.content = AnyView(content())
    }

    var body: some View {
        if isActive {
            content
                .transition(.opacity)
        } else {
            ZStack {
                TvManiacAppIcon.image(isDebug: isDebug)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 180, height: 180)
                    .clipShape(Circle())
                    .scaleEffect(logoScale)
                    .opacity(logoOpacity)
            }
            .appScreen()
            .onAppear {
                withAnimation(.easeOut(duration: 0.4)) {
                    logoScale = 1.0
                    logoOpacity = 1.0
                }
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.6) {
                    withAnimation(.easeInOut(duration: 0.2)) {
                        isActive = true
                    }
                }
            }
        }
    }
}
