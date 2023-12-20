//
//  HeaderPlaceholder.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct HeaderPlaceholder: View {
    var shadowRadius: CGFloat = 2
    var posterRadius: CGFloat = 8
    var posterWidth: CGFloat = UIScreen.main.bounds.width
    var posterHeight: CGFloat

    var body: some View {
        ZStack {
            Rectangle().fill(.gray.gradient)
            VStack {
                Image(systemName: "popcorn.fill")
                    .font(.title)
                    .fontWidth(.expanded)
                    .foregroundColor(.white.opacity(0.8))
                    .frame(width: 120, height: 120)
                    .padding()
            }
        }
        .frame(width: posterWidth,height: posterHeight)
        .clipShape(RoundedRectangle(cornerRadius: posterRadius, style: .continuous))
        .shadow(radius: shadowRadius)
    }
}
