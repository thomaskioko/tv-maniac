//
//  CoverArtWorkView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SDWebImageSwiftUI
import TvManiac

struct HeaderCoverArtWorkView: View {
    var backdropImageUrl : String?
    var posterHeight: CGFloat
    
    var body: some View {
        if let imageUrl = backdropImageUrl {
            WebImage(url: URL(string: imageUrl), options: .highPriority)
                .resizable()
                .placeholder {
                    headerPosterPlaceholder
                }
                .aspectRatio(contentMode: .fill)
                .transition(.opacity)
                .frame(width: DimensionConstants.posterWidth, height: posterHeight)
            
        } else {
            headerPosterPlaceholder
        }
    }
    
    private var headerPosterPlaceholder : some View {
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
        .frame(width: DimensionConstants.posterWidth, height: posterHeight)
        .clipShape(RoundedRectangle(cornerRadius: DimensionConstants.cornerRadius, style: .continuous))
        .shadow(radius: DimensionConstants.shadowRadius)
    }
}

private struct DimensionConstants {
    static let posterWidth: CGFloat = UIScreen.main.bounds.width
    static let shadowRadius: CGFloat = 2
    static let cornerRadius: CGFloat = 8
}
