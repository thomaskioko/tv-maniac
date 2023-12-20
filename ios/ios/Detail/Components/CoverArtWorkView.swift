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

struct CoverArtWorkView: View {
    var backdropImageUrl : String?
    var posterHeight: CGFloat
    
    var body: some View {
        if let imageUrl = backdropImageUrl {
            WebImage(url: URL(string: imageUrl), options: .highPriority)
                .resizable()
                .placeholder {
                    HeaderPlaceholder(posterHeight: posterHeight)
                }
                .aspectRatio(contentMode: .fill)
                .transition(.opacity)
                .frame(width: DrawingConstants.posterWidth, height: posterHeight)
            
        } else {
            HeaderPlaceholder(posterHeight: posterHeight)
        }
    }
}

private struct DrawingConstants {
    static let posterWidth: CGFloat = UIScreen.main.bounds.width
    static let shadowRadius: CGFloat = 2
}
