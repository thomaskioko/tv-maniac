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

    var topBarTitle: String
    var imageName: String
    var onBackClicked: () -> Void
    var width: CGFloat = 40
    var height: CGFloat = 40

    var body: some View {
        ZStack{
            Color.background
                .shadow(color: Color.grey_200, radius: 10, x: 0, y: 5)

            VStack {
                HStack {
                    Button(action: onBackClicked) {
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
                        }
                    }
                    .buttonStyle(PlainButtonStyle())
                    .padding(.leading, 16)

                    Spacer()

                    Text(topBarTitle)
                        .font(.title)
                        .bold()

                    Spacer()

                    // Placeholder for a possible trailing button
                    Spacer()
                        .frame(width: 40)
                }
                .padding(.bottom, 10) // Adjust the bottom padding for desired height
//
//                .shadow(color: Color.background, radius: 10, x: 0, y: 5)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .padding(.top, 50) // Adjust the top padding for status bar spacing
        }
        .frame(height: 100)

    }
}

struct CustomNavigationBar: View {
    @Environment(\.colorScheme) var colorScheme // Detect the current color scheme

    var body: some View {
        ZStack {
            Color.white
                .shadow(color: .gray, radius: 10, x: 0, y: 5)

            VStack {
                Spacer() // Push content to the bottom

                HStack {
                    Button(action: {
                        // Action for the button
                        print("Back button tapped")
                    }) {
                        ZStack {
                            Circle()
                                .fill(colorScheme == .dark ? Color.gray : Color.white)
                                .frame(width: 40, height: 40)

                            Image(systemName: "arrow.left")
                                .foregroundColor(.blue)
                                .font(.system(size: 20, weight: .bold))
                        }
                    }
                    .buttonStyle(PlainButtonStyle())
                    .padding(.leading, 20)

                    Spacer()

                    Text("Home")
                        .font(.largeTitle)
                        .bold()

                    Spacer()

                    // Placeholder for a possible trailing button
                    Spacer()
                        .frame(width: 40)
                }
                .padding(.bottom, 10) // Adjust the bottom padding to align content properly
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .padding(.top, 50) // Adjust the top padding for status bar spacing
        }
        .frame(height: 100) // Adjust the height of the custom navigation bar
    }
}


struct NavigationTopBar_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            NavigationTopBar(topBarTitle: "Upcoming", imageName: "arrow.backward", onBackClicked: {})

            Spacer()
        }
        .edgesIgnoringSafeArea(.top)
        .background(Color.background)
    }
}
