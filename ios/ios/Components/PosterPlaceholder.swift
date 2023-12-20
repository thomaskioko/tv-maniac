//
//  PosterPlaceholder.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct PosterPlaceholder: View {
    var title: String
    var shadowRadius: CGFloat = 2
    var posterRadius: CGFloat = 8
    var posterWidth: CGFloat = 160
    var posterHeight: CGFloat = 240

    var body: some View {
        ZStack {
            Rectangle().fill(.gray.gradient)
            VStack {
                Image(systemName: "popcorn.fill")
                    .font(.title)
                    .fontWidth(.expanded)
                    .foregroundColor(.white.opacity(0.8))
                    .padding()
                
                Text(title)
                    .font(.callout)
                    .foregroundColor(.white.opacity(0.8))
                    .lineLimit(2)
                    .padding(.bottom)
                    .padding(.horizontal, 4)
                
            }
        }
        .frame(width: posterWidth,height: posterHeight)
        .clipShape(RoundedRectangle(cornerRadius: posterRadius, style: .continuous))
        .shadow(radius: shadowRadius)
    }
}
