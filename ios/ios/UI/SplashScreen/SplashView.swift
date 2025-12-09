//
//  SplashView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 12/8/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents

struct SplashView: View {
    @Theme private var theme
    @State private var isActive = false
    @State private var logoScale: CGFloat = 0.6
    @State private var logoOpacity: Double = 0
    private let content: AnyView

    init(@ViewBuilder content: @escaping () -> some View) {
        self.content = AnyView(content())
    }

    var body: some View {
        if isActive {
            content
                .transition(.opacity)
        } else {
            ZStack {
                theme.colors.background
                    .ignoresSafeArea()

                Image("TvManiacIcon")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 180, height: 180)
                    .clipShape(Circle())
                    .scaleEffect(logoScale)
                    .opacity(logoOpacity)
            }
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
