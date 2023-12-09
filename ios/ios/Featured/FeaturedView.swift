//
//  FeaturedView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 09.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct FeaturedView: View {
    
    @Environment(\.colorScheme) var scheme
    @State var currentIndex: Int = 2
    
    var tvShows: [TvShow]?
    var onClick : (Int64) -> Void
    
    var body: some View {
        
        ZStack {
            BackgroundView(tvShows: tvShows)
       
            if let shows = tvShows {
                if !shows.isEmpty {
                    SnapCarousel(spacing: 10, trailingSpace: 120,index: $currentIndex, items: shows) { show  in
                        
                        GeometryReader{ proxy in
                            
                            let size = proxy.size
                            
                            ShowPosterImage(
                                posterSize: .big,
                                imageUrl: show.posterImageUrl,
                                showTitle: show.title,
                                showId: show.traktId,
                                onClick: { onClick(show.traktId)  }
                            )
                            .frame(width: size.width, height: size.height)
                            .cornerRadius(12)
                            .shadow(color: Color("shadow1"), radius: 4, x: 0, y: 4)
                            .transition(AnyTransition.slide)
                            .animation(.spring())
                        }
                    }
                   
                    
                    
                    CustomIndicator(shows: shows)
                        .padding()
                        .padding(.top, 10)
                }
            }
        } .frame(height: 450)
            .padding(.top, 80)
    }
    
    @ViewBuilder
    func CustomIndicator(shows: [TvShow]) -> some View {
        HStack(spacing: 5) {
            ForEach(shows.indices, id: \.self) { index in
                Circle()
                    .fill(currentIndex == index ? Color.accent_color : .gray.opacity(0.5))
                    .frame(width: currentIndex == index ? 10 : 6, height: currentIndex == index ? 10 : 6)
            }
        }
        .animation(.easeInOut, value: currentIndex)
    }
    
    
    @ViewBuilder
    func BackgroundView(tvShows: [TvShow]?) -> some View {
        if let shows = tvShows {
            if !shows.isEmpty {
                GeometryReader { proxy in
                    let size = proxy.size
                    
                    TabView(selection: $currentIndex) {
                        ForEach(shows.indices, id: \.self) { index in
                            ShowPosterImage(
                                posterSize: .big,
                                imageUrl: shows[index].posterImageUrl,
                                showTitle: shows[index].title,
                                showId: shows[index].traktId,
                                onClick: {  }
                            )
                            .aspectRatio(contentMode: .fill)
                            .frame(width: size.width + 250, height: size.height + 200)
                            .tag(index)
                        }
                        
                    }
                    .tabViewStyle(.page(indexDisplayMode: .never))
                    .animation(.easeInOut, value: currentIndex)
                    
                    
                    let color: Color = (scheme == .dark ? .black : .white)
                    // Custom Gradient
                    LinearGradient(colors: [
                        .black,
                        .clear,
                        color.opacity(0.15),
                        color.opacity(0.5),
                        color.opacity(0.8),
                        color,
                        color
                    ], startPoint: .top, endPoint: .bottom)
                    
                    // Blurred Overlay
                    Rectangle()
                        .fill(.ultraThinMaterial)
                }
                .ignoresSafeArea()
            }
        }
    }
}
