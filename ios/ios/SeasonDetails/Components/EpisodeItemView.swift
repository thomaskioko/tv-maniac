//
//  EpisodeItemView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SDWebImageSwiftUI
import TvManiac

struct EpisodeItemView: View {
    
    var imageUrl: String?
    var episodeTitle: String
    var episodeOverView: String
    
    var body: some View {
        HStack {
            if let imageUrl = imageUrl {
                WebImage(url: URL(string: imageUrl))
                    .resizable()
                    .placeholder { episodePlaceholder }
                    .aspectRatio(contentMode: .fill)
                    .frame(
                        width: DimensionConstants.episodeWidth,
                        height: DimensionConstants.episodeHeight
                    )
                    .clipShape(
                        RoundedRectangle(
                            cornerRadius: DimensionConstants.cornerRadius,
                            style: .continuous
                        )
                    )
            } else {
                episodePlaceholder
            }
            
            VStack {
                
                Text(episodeTitle)
                    .font(.title3)
                    .fontWeight(.semibold)
                    .lineLimit(1)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.bottom, 0.5)
                    .padding(.top, 16)
                
                
                Text(episodeOverView)
                    .font(.callout)
                    .padding([.top], 2)
                    .lineLimit(4)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .leading)
                
                Spacer()
            }
            .padding(.horizontal, 8)
            
       
            
        }
        .frame(height: DimensionConstants.episodeHeight)
        .background(Color.content_background)
        .cornerRadius(4)
        .padding(.horizontal)
        
    }
    
    private var episodePlaceholder: some View {
        ZStack {
            ZStack {
                Rectangle().fill(.gray.gradient)
                Image(systemName: "popcorn.fill")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 50, height: 50, alignment: .center)
                    .foregroundColor(.white)
                
            }
            .frame(width: DimensionConstants.episodeWidth,
                   height: DimensionConstants.episodeHeight)
            .clipShape(
                RoundedRectangle(
                    cornerRadius: DimensionConstants.cornerRadius,
                    style: .continuous
                )
            )
            .shadow(radius: DimensionConstants.shadowRadius)
        }
    }
}

private struct DimensionConstants {
    static let episodeWidth: CGFloat = 120
    static let episodeHeight: CGFloat = 140
    static let shadowRadius: CGFloat = 2.5
    static let cornerRadius: CGFloat = 2
    static let lineLimit: Int = 1
}


#Preview {
    EpisodeItemView(
        imageUrl: "https://image.tmdb.org/t/p/w500/path/to/image.jpg",
        episodeTitle: "E01 • Glorious Purpose",
        episodeOverView: "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority."
    )
}
