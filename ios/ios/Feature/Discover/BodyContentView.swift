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

    let showResult: DiscoverShowsState.DiscoverShowResult


    var body: some View {
        ZStack {
            BackgroundView()

            ScrollView(showsIndicators: false) {

                VStack {

                    /**
                     * This is a temporary implementation for navigation to the detail view. The problem is NavigationView
                     * does not support MatchedGeometry effect. The other alternative would be to use an overlay
                     * for the detailView.
                     */
                    NavigationLink(
                            destination: ShowDetailView(showId: showResult.featuredShows.tvShows[currentIndex].id)
                    ) {
                        SnapCarousel(
                                spacing: 10,
                                trailingSpace: 110,
                                index: $currentIndex,
                                items: showResult.featuredShows.tvShows
                        ) { show in

                            GeometryReader { proxy in
                                let size = proxy.size

                                ShowPosterImage(
                                        posterSize: .big,
                                        imageUrl: show.posterImageUrl
                                )
                                        .frame(width: size.width, height: size.height)
                                        .matchedGeometryEffect(id: show.id, in: animation)
                            }
                        }
                    }
                            .frame(height: 450)
                            .padding(.top, 120)

                    CustomIndicator()

                    //Trending
                    ShowRow(
                            categoryName: showResult.trendingShows.category.title,
                            shows: showResult.trendingShows.tvShows
                    )

                    //Top Rated
                    ShowRow(
                            categoryName: showResult.topRatedShows.category.title,
                            shows: showResult.topRatedShows.tvShows
                    )
                    //Popular
                    ShowRow(
                            categoryName: showResult.popularShows.category.title,
                            shows: showResult.popularShows.tvShows
                    )

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
                ForEach(showResult.featuredShows.tvShows.indices, id: \.self) { index in
                    ShowPosterImage(
                            posterSize: .big,
                            imageUrl: showResult.featuredShows.tvShows[index].posterImageUrl
                    )
                            .frame(width: size.width, height: size.height)
                            .clipped()
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

    @ViewBuilder
    func CustomIndicator() -> some View {
        HStack(spacing: 5) {
            ForEach(showResult.featuredShows.tvShows.indices, id: \.self) { index in
                Circle()
                        .fill(currentIndex == index ? Color.accent_color : .gray.opacity(0.5))
                        .frame(width: currentIndex == index ? 10 : 6, height: currentIndex == index ? 10 : 6)
            }
        }
                .animation(.easeInOut, value: currentIndex)
    }

}
