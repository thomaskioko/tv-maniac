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

    let contentState: ShowsLoaded

    var body: some View {
        ZStack {
            BackgroundView()

            ScrollView(showsIndicators: false) {

                VStack {

                    switch contentState.result.featuredCategoryState {
                    case is ShowResult.CategoryError:
                        let state = contentState.result.featuredCategoryState as! ShowResult.CategoryError
                        ErrorView(errorMessage: state.errorMessage)
                    case is ShowResult.CategorySuccess:
                        let state = contentState.result.featuredCategoryState as! ShowResult.CategorySuccess
                        if state.tvShows.isEmpty == false {
                            /**
							 * This is a temporary implementation for navigation to the detail view. The problem is NavigationView
							 * does not support MatchedGeometry effect. The other alternative would be to use an overlay
							 * for the detailView.
							 */
                            NavigationLink(destination: ShowDetailView(showId: state.tvShows[currentIndex].traktId)) {
                                SnapCarousel(
                                        spacing: 10,
                                        trailingSpace: 70,
                                        index: $currentIndex,
                                        items: state.tvShows
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
                                    .padding(.top, 120)

                            CustomIndicator()

                        }
                    default:
                        let _ = print("Unhandled case: \(contentState.result.featuredCategoryState)")
                    }

                    switch contentState.result.trendingCategoryState {
                    case is ShowResult.CategoryError:
                        let state = contentState.result.trendingCategoryState as! ShowResult.CategoryError
                        ErrorView(errorMessage: state.errorMessage)
                    case is ShowResult.CategorySuccess:
                        let state = contentState.result.trendingCategoryState as! ShowResult.CategorySuccess
                        ShowRow(
                                categoryName: state.category.title,
                                shows: state.tvShows
                        )
                    default:
                        let _ = print("Unhandled case: \(contentState.result.trendingCategoryState)")
                    }
					
					switch contentState.result.anticipatedCategoryState {
					case is ShowResult.CategoryError:
						let state = contentState.result.anticipatedCategoryState as! ShowResult.CategoryError
						ErrorView(errorMessage: state.errorMessage)
					case is ShowResult.CategorySuccess:
						let state = contentState.result.anticipatedCategoryState as! ShowResult.CategorySuccess
						ShowRow(
								categoryName: state.category.title,
								shows: state.tvShows
						)
					default:
						let _ = print("Unhandled case: \(contentState.result.anticipatedCategoryState)")
					}


                    switch contentState.result.popularCategoryState {
                    case is ShowResult.CategoryError:
                        let state = contentState.result.popularCategoryState as! ShowResult.CategoryError
                        ErrorView(errorMessage: state.errorMessage)
                    case is ShowResult.CategorySuccess:
                        let state = contentState.result.popularCategoryState as! ShowResult.CategorySuccess
                        ShowRow(
                                categoryName: state.category.title,
                                shows: state.tvShows
                        )
                    default:
                        let _ = print("Unhandled case: \(contentState.result.popularCategoryState)")
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
                if contentState.result.featuredCategoryState is ShowResult.CategorySuccess {
                    let state = contentState.result.featuredCategoryState as! ShowResult.CategorySuccess
                    ForEach(state.tvShows.indices, id: \.self) { index in
                        ShowPosterImage(
                                posterSize: .big,
                                imageUrl: state.tvShows[index].posterImageUrl
                        )
                                .frame(width: size.width, height: size.height)
                                .clipped()
                                .tag(index)
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
            if contentState.result.featuredCategoryState is ShowResult.CategorySuccess {
                let state = contentState.result.featuredCategoryState as! ShowResult.CategorySuccess
                ForEach(state.tvShows.indices, id: \.self) { index in
                    Circle()
                            .fill(currentIndex == index ? Color.accent_color : .gray.opacity(0.5))
                            .frame(width: currentIndex == index ? 10 : 6, height: currentIndex == index ? 10 : 6)
                }
            }

        }
                .animation(.easeInOut, value: currentIndex)
    }

}
