//
//  ErrorUiView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/10/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI


struct ErrorUiView: View {
    
    public var systemImage: String? = nil
    public let action: () -> Void
    
    var body: some View {
        VStack(spacing: 20) {
            
            if let image = systemImage {
                Image(systemName: image)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 120, height: 120)
                    .foregroundColor(.accent)
            }

            
            Text("Something went wrong!!")
                .titleBoldFont(size: 24)
                .font(.title)
                .frame(maxWidth: .infinity)
            
            Button(
                action: { action() },
                label: {
                    Text("Retry")
                        .bodyMediumFont(size: 16)
                        .padding()
                        .padding([.leading, .trailing], 24)
                        .foregroundColor(Color.text_color_bg)
                        .background(Color.accent)
                        .cornerRadius(10)
                })
            .shadow(radius: 10)
            
        }
        .padding()
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .edgesIgnoringSafeArea(.all)
    }
}

#Preview {
    ForEach(ColorScheme.allCases, id: \.self) { colorScheme in
        ErrorUiView(
            action: {}
        )
        .preferredColorScheme(colorScheme)
        .previewDisplayName("\(colorScheme == .light ? "Light Mode" : "Dark Mode")")
    }
}
