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

struct ItemContentPosterView: View {
    let show: DiscoverShow
    
    var body: some View {
        if let posterUrl = show.posterImageUrl {
            WebImage(url: URL(string: posterUrl), options: .highPriority)
                .resizable()
                .placeholder {
                    PosterPlaceholder(title: show.title)
                }
                .aspectRatio(contentMode: .fill)
                .overlay {
                    if show.isInLibrary {
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
                        .frame(width: DrawingConstants.posterWidth)
                    }
                }
                .transition(.opacity)
                .frame(width: DrawingConstants.posterWidth, height: DrawingConstants.posterHeight
                )
                .clipShape(RoundedRectangle(cornerRadius: DrawingConstants.posterRadius,
                                            style: .continuous))
        } else {
            PosterPlaceholder(title: show.title)
        }
        
    }
}

private struct DrawingConstants {
    static let posterWidth: CGFloat = 120
    static let posterHeight: CGFloat = 180
    static let posterRadius: CGFloat = 4
    static let shadowRadius: CGFloat = 2
}
