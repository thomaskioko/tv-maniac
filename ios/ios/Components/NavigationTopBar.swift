//
//  NaviationTopBar.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/7/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct NavigationTopBar: View {

    @Environment(\.colorScheme) var colorScheme

    var topBarTitle: String? = nil
    var imageName: String = "arrow.backward"
    var onBackClicked: () -> Void
    var width: CGFloat = 40
    var height: CGFloat = 40
    @State private var isButtonPressed = false

    var body: some View {
        ZStack{
            Color.background
                .shadow(color: Color.grey_200, radius: 10, x: 0, y: 5)

            VStack {
                HStack {
                    Button(action:  {
                        isButtonPressed = true
                        onBackClicked()
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                            isButtonPressed = false
                        }
                    }) {
                        ZStack {
                            Circle()
                                .fill(colorScheme == .dark ? Color.white : .gray.opacity(0.8))
                                .overlay(
                                    Image(systemName: imageName)
                                        .resizable()
                                        .scaledToFit()
                                        .foregroundColor(colorScheme == .dark ? Color.black : Color.white)
                                        .font(.system(size: 20, weight: .bold))
                                        .padding(12)
                                )
                                .frame(width: width, height: height)
                                .buttonElevationEffect(isPressed: $isButtonPressed)
                        }
                    }
                    .buttonStyle(PlainButtonStyle())
                    .padding(.leading, 16)

                    Spacer()

                    if let title = topBarTitle {
                        Text(title)
                            .font(.title)
                            .bold()

                        Spacer()
                    }

                    
                    // Placeholder for a possible trailing button
                    Spacer()
                        .frame(width: 40)
                }
                .padding(.bottom, 10) // Adjust the bottom padding for desired height
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .padding(.top, 60) // Adjust the top padding for status bar spacing
        }
        .frame(height: 100)

    }
}

struct NavigationTopBar_Previews: PreviewProvider {
    static var previews: some View {
        ForEach(ColorScheme.allCases, id: \.self) { colorScheme in
            VStack {
                NavigationTopBar(topBarTitle: "Upcoming", imageName: "arrow.backward", onBackClicked: {})

                Spacer()
            }
            .edgesIgnoringSafeArea(.top)
            .background(Color.background)
            .preferredColorScheme(colorScheme)
            .previewDisplayName("\(colorScheme == .light ? "Light Mode" : "Dark Mode")")
        }
    }
}
