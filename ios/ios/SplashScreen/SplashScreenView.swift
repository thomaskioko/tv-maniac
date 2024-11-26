//
//  SplashScreenView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/25/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct SplashScreenView: View {
    var body: some View {
        ZStack {
            Color.background

            VStack {
                Image("TvManiacIcon")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 180, height: 180)
                    .clipShape(Circle())
            }
        }
        .ignoresSafeArea()
    }
}
