//
//  ItemContentPosterView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SDWebImageSwiftUI
import TvManiac

struct PosterItemView: View {
    let showId: Int64
    let title: String
    let posterUrl: String?
    var isInLibrary: Bool = false
    var posterWidth: CGFloat = 120
    var posterHeight: CGFloat = 180
    
    var body: some View {
        if let posterUrl = posterUrl {
            WebImage(url: URL(string: posterUrl), options: .highPriority)
                .resizable()
                .placeholder {
                    PosterPlaceholder(
                        title: title,
                        posterWidth: posterWidth,
                        posterHeight: posterHeight
                    )
                }
                .aspectRatio(contentMode: .fill)
                .overlay {
                    if isInLibrary{
                        VStack {
                            Spacer()
                            HStack {
                                Spacer()
                                
                                Image(systemName: "square.stack.fill")
                                    .imageScale(.medium)
                                    .foregroundColor(.white.opacity(0.9))
                                    .padding([.vertical])
                                    .padding(.trailing, 16)
                                    .font(.caption)
                            }
                            .background {
                                Color.black.opacity(0.6)
                                    .mask {
                                        LinearGradient(colors:
                                                        [Color.black,
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
                .frame(width: posterWidth, height: posterHeight)
                .clipShape(
                    RoundedRectangle(
                        cornerRadius: DimensionConstants.posterRadius,
                        style: .continuous
                    )
                )
        } else {
            PosterPlaceholder(
                title: title,
                posterWidth: posterWidth,
                posterHeight: posterHeight
            )
        }
        
    }
}

private struct DimensionConstants {
    static let posterRadius: CGFloat = 4
    static let shadowRadius: CGFloat = 2
}
