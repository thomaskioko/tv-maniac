//
//  SplashView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 12/8/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct SplashView: View {
    @State private var isActive = false
    private let content: AnyView

    init<Content: View>(@ViewBuilder content: @escaping () -> Content) {
        self.content = AnyView(content())
    }

    var body: some View {
        if isActive {
          ZStack {
            content
          }
        } else {
            VStack {
              Image("TvManiacIcon")
                  .resizable()
                  .scaledToFit()
                  .frame(width: 180, height: 180)
                  .clipShape(Circle())
            }
            .onAppear {
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                    withAnimation {
                        self.isActive = true
                    }
                }
            }
        }
    }
}
