import SwiftUI
import TvManiac
import Kingfisher

struct DiscoverView: View {


    @ObservedObject var observable = ObservableViewModel<DiscoverShowsViewModel, DiscoverShowsState>(
            viewModel: DiscoverShowsViewModel()
    )


    var body: some View {
        NavigationView {
            ZStack {
                Color("Background")
                        .edgesIgnoringSafeArea(.all)

                if observable.state is DiscoverShowsState.InProgress {
                    LoadingIndicatorView()
                } else if observable.state is DiscoverShowsState.Error {
                    //TODO:: Show Error
                    EmptyView()
                } else if observable.state is DiscoverShowsState.Success {

                    VStack {
                        ScrollView(showsIndicators: false) {

                            if let result = observable.state as? DiscoverShowsState.Success {

								FeaturedShowsView(shows: result.data.featuredShows.tvShows)

                                ShowRow(
                                        categoryName: result.data.trendingShows.category.title,
                                        shows: result.data.trendingShows.tvShows
                                )

                                ShowRow(
                                        categoryName: result.data.topRatedShows.category.title,
                                        shows: result.data.topRatedShows.tvShows
                                )

                                ShowRow(
                                        categoryName: result.data.popularShows.category.title,
                                        shows: result.data.popularShows.tvShows
                                )
                            }
                            Spacer()
                        }
                    }
                }
            }
                    .onAppear {
                        observable.viewModel.attach()
                    }
                    .onDisappear {
                        observable.viewModel.detach()
                    }
                    .navigationBarHidden(true)
        }
                .accentColor(.white)
                .navigationViewStyle(StackNavigationViewStyle())
    }

}

struct FeaturedShowsView: View {

    @SwiftUI.State var currentIndex: Int = 2

    let shows: [TvShow]
    let resizingProcessor = ResizingImageProcessor(
            referenceSize: CGSize(width: PosterStyle.Size.big.width(), height: PosterStyle.Size.big.height())
    ) |> RoundCornerImageProcessor(cornerRadius: 5)

    var body: some View {

        ZStack {
            if shows.count != 0 {
				
                TabView(selection: $currentIndex) {
                    NavigationLink(destination: ShowDetailView(showId: shows[currentIndex].id)) {
                        SnapCarousel(
                                spacing: getRect().height < 750 ? 15 : 20,
                                trailingSpace: getRect().height < 750 ? 100 : 150,
                                index: $currentIndex,
                                items: shows
                        ) { show in
                            CardView(show: show)
                        }
                                .offset(y: getRect().height / 5.5)
                    }
				}
                        .ignoresSafeArea()
                        .indexViewStyle(PageIndexViewStyle(backgroundDisplayMode: .always))
                        .tabViewStyle(PageTabViewStyle(indexDisplayMode: .always))
                        .onAppear {
                            UIPageControl.appearance().currentPageIndicatorTintColor = .white
                            UIPageControl.appearance().pageIndicatorTintColor = UIColor.black.withAlphaComponent(0.2)
                        }
            }
        }
                .frame(height: 550)
    }

    @ViewBuilder
    func CardView(show: TvShow) -> some View {

        VStack(spacing: 10) {
            GeometryReader { proxy in
                ShowPosterImage(
                        processor: resizingProcessor,
                        posterSize: .big,
                        imageUrl: show.posterImageUrl
                )
                        .frame(height: getRect().height / 2.5)
            }
        }
    }

}


// Screen Bounds...
extension View {
    func getRect() -> CGRect {
        UIScreen.main.bounds
    }
}


struct DiscoverView_Previews: PreviewProvider {

    static var previews: some View {
        DiscoverView()

        DiscoverView()
                .preferredColorScheme(.dark)
    }
}
