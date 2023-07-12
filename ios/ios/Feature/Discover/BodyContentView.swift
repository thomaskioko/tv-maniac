//
// Created by Thomas Kioko on 08.04.22.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct BodyContentView: View {
    
    // Environment Values
    @Namespace var animation
    @Environment(\.colorScheme) var scheme
    
    @SwiftUI.State var currentIndex: Int = 2
    
    let contentState: DiscoverState
    
    var body: some View {
        ZStack {
            BackgroundView()
            
            ScrollView(showsIndicators: false) {
                
                VStack {
                    
                    switch contentState {
                    case is DataLoaded:
                        let state = contentState as! DataLoaded
                        if(state.isContentEmpty && state.errorMessage != nil ){
                            ErrorView(errorMessage: state.errorMessage!)
                        }
                        if(contentState.isContentEmpty){
                            ErrorView(errorMessage: "")
                        }
                        
                        
                        //Featured Content
                        if let tvShows = state.recommendedShows {
                            if !tvShows.isEmpty {
                                /**
                                 * This is a temporary implementation for navigation to the detail view. The problem is NavigationView
                                 * does not support MatchedGeometry effect. The other alternative would be to use an overlay
                                 * for the detailView.
                                 */
                                NavigationLink(destination: ShowDetailView(showId: (tvShows[currentIndex].traktId))) {
                                    SnapCarousel(
                                        spacing: 10,
                                        trailingSpace: 70,
                                        index: $currentIndex,
                                        items: tvShows
                                    ) { show in
                                        
                                        GeometryReader { proxy in
                                            let size = proxy.size
                                            
                                            ShowPosterImage(
                                                posterSize: .big,
                                                imageUrl: show.posterImageUrl
                                            )
                                            .frame(width: size.width, height: size.height)
                                            .matchedGeometryEffect(id: show.traktId, in: animation)
                                        }
                                    }
                                }
                                .frame(height: 450)
                                .padding(.top, 140)
                                
                                CustomIndicator()
                            }
                        }
                        
                        
                        //Anticipated shows
                        ShowRow(
                            categoryName: "Anticipated",
                            shows: state.anticipatedShows
                        )
                        
                        //Trendings shows
                        ShowRow(
                            categoryName: "Trending",
                            shows: state.trendingShows
                        )
                        
                        //Popular Shows
                        ShowRow(
                            categoryName: "Popular",
                            shows: state.popularShows
                        )
                        
                    default:
                        let _ = print("Unhandled case: \(contentState)")
                    }
                }
                .padding(.bottom, 90)
            }
        }
    }
    
    @ViewBuilder
    func BackgroundView() -> some View {
        GeometryReader { proxy in
            let size = proxy.size
            
            TabView(selection: $currentIndex) {
                if contentState is DataLoaded {
                    let state = contentState as! DataLoaded
                    
                    if let tvShows = state.recommendedShows {
                        if !tvShows.isEmpty {
                            ForEach(tvShows.indices, id: \.self) { index in
                                ShowPosterImage(
                                    posterSize: .big,
                                    imageUrl: tvShows[index].posterImageUrl
                                )
                                .frame(width: size.width, height: size.height)
                                .clipped()
                                .tag(index)
                            }
                        }
                    }
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
    
    @ViewBuilder
    func CustomIndicator() -> some View {
        HStack(spacing: 5) {
            if contentState is DataLoaded {
                let state = contentState as! DataLoaded
                if let tvShows = state.recommendedShows {
                    if !tvShows.isEmpty {
                        ForEach(tvShows.indices, id: \.self) { index in
                            Circle()
                                .fill(currentIndex == index ? Color.accent_color : .gray.opacity(0.5))
                                .frame(width: currentIndex == index ? 10 : 6, height: currentIndex == index ? 10 : 6)
                        }
                    }
                }
                
            }
            
        }
        .animation(.easeInOut, value: currentIndex)
    }
    
}
