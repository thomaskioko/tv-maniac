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
            Color.background // You can change this to your app's primary color

            VStack {
                Image("TvManiacIcon") // Replace "AppIcon" with the name of your icon in assets
                    .resizable()
                    .scaledToFit()
                    .frame(width: 180, height: 180)
                    .clipShape(Circle())
            }
        }
        .ignoresSafeArea()
    }
}
