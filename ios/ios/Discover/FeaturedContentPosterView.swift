//
//  FeaturedContentPosterView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SDWebImageSwiftUI
import TvManiac

struct FeaturedContentPosterView: View {
    let show: DiscoverShow
    var onClick: (Int64) -> Void
    var posterWidth: CGFloat = 260
    
    var body: some View {
        if let posterUrl = show.posterImageUrl {
            WebImage(url: URL(string: posterUrl), options: .highPriority)
                .resizable()
                .placeholder {
                    PosterPlaceholder(
                        title: show.title,
                        posterRadius: DimensionConstants.posterRadius,
                        posterWidth: posterWidth,
                        posterHeight: DimensionConstants.posterHeight
                    )
                }
                .aspectRatio(contentMode: .fill)
                .overlay {
                    if show.inLibrary {
                        VStack {
                            Spacer()
                            HStack {
                                Spacer()
                                
                                Image(systemName: "square.stack.fill")
                                    .imageScale(.large)
                                    .foregroundColor(.white.opacity(0.9))
                                    .padding([.vertical])
                                    .padding(.trailing, 16)
                                    .font(.caption)
                            }
                            .background {
                                Color.black.opacity(0.6)
                                    .mask {
                                        LinearGradient(colors: [Color.black,
                                                         Color.black.opacity(0.924),
                                                         Color.black.opacity(0.707),
                                                         Color.black.opacity(0.383),
                                                         Color.black.opacity(0)],
                                                       startPoint: .bottom,
                                                       endPoint: .top)
                                    }
                            }
                        }
                        .frame(width: posterWidth)
                    }
                }
                .transition(.opacity)
                .frame(width: posterWidth, height: DimensionConstants.posterHeight)
                .clipShape(
                    RoundedRectangle(cornerRadius: DimensionConstants.posterRadius, style: .continuous)
                )
                .onTapGesture { onClick(show.tmdbId) }
        } else {
            PosterPlaceholder(title: show.title)
                .onTapGesture { onClick(show.tmdbId) }
        }
        
    }
}

private struct DimensionConstants {
    static let posterHeight: CGFloat = 460
    static let posterRadius: CGFloat = 12
    static let shadowRadius: CGFloat = 2
}
