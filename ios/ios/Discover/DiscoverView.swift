import SwiftUI
import TvManiac
import os.log

struct DiscoverView: View {
    
    @Environment(\.colorScheme) var scheme
    @State private var currentIndex: Int = 2
    @ObservedObject @StateFlow private var uiState: DiscoverState

    private let presenter: DiscoverShowsPresenter

    init(presenter: DiscoverShowsPresenter){
        self.presenter = presenter
        self._uiState = .init(presenter.state)
    }

    var body: some View {
        VStack {
            switch onEnum(of: uiState) {
                case .loading:
                LoadingIndicatorView()
                    .frame(maxWidth: UIScreen.main.bounds.width, maxHeight: UIScreen.main.bounds.height,  alignment: .center)
                case .dataLoaded(let data): LoadedContent(data)
                case .emptyState: emptyView
                case .errorState(let error):  FullScreenView(
                    systemName: "exclamationmark.arrow.triangle.2.circlepath",
                    message: error.errorMessage ?? "Something went wrong!!"
                )
            }
        }
    }
    
    @ViewBuilder
    private var emptyView : some View {
        VStack {
            Image(systemName: "list.bullet.below.rectangle")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .foregroundColor(Color.accent)
                .font(Font.title.weight(.thin))
                .frame(width: 160, height: 180)
            
            Text("Looks like your stash is empty")
                .titleSemiBoldFont(size: 18)
                .padding(.top, 8)
            
            Text("Could be that you forgot to add your TMDB API Key. Once you set that up, you can get lost in the vast world of Tmdb's collection.")
                .captionFont(size: 16)
                .padding(.top, 1)
                .padding(.bottom, 16)
            
            Button(action: {
                presenter.dispatch(action: RefreshData())
            }, label: {
                Text("Retry")
                    .bodyMediumFont(size: 16)
                    .foregroundColor(Color.accent)
            })
            .buttonStyle(BorderlessButtonStyle())
            .padding(16)
            .background(
                RoundedRectangle(cornerRadius: 5)
                    .stroke(Color.accent, lineWidth: 2)
                    .background(.clear)
                    .cornerRadius(2))
            
        }
        .frame(maxWidth: .infinity,maxHeight: .infinity)
        .padding([.trailing, .leading], 16)
    }
    
    @ViewBuilder
    private func LoadedContent(_ contentState: DataLoaded) -> some View {
        ZStack {
            BackgroundView(contentState.featuredShows)
            
            ScrollView(showsIndicators: false) {
                
                let state = contentState
                
                if(state.errorMessage != nil) {
                    FullScreenView(systemName: "exclamationmark.triangle", message: state.errorMessage!)
                } else {
                    
                    FeaturedContentView(state.featuredShows)

                    HorizontalItemContentListView(
                        items: state.trendingToday,
                        title: "Trending Today",
                        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
                        onMoreClicked: { presenter.dispatch(action: TrendingClicked()) }
                    )

                    HorizontalItemContentListView(
                        items: state.upcomingShows,
                        title: "Upcoming",
                        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
                        onMoreClicked: { presenter.dispatch(action: UpComingClicked()) }
                    )
                    
                    HorizontalItemContentListView(
                        items: state.popularShows,
                        title: "Popular",
                        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
                        onMoreClicked: { presenter.dispatch(action: PopularClicked()) }
                    )
                    
                    HorizontalItemContentListView(
                        items: state.topRatedShows,
                        title: "Top Rated",
                        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
                        onMoreClicked: { presenter.dispatch(action: TopRatedClicked()) }
                    )
                }
            }
            .refreshable {
                presenter.dispatch(action: RefreshData())
            }
        }
    }
    
    
    @ViewBuilder
    func FeaturedContentView(_ shows: [DiscoverShow]?) -> some View {
        if let shows = shows {
            if !shows.isEmpty {
                SnapCarousel(spacing: 10, trailingSpace: 140, index: $currentIndex, items: shows) { show  in

                    GeometryReader{ proxy in
                        
                        FeaturedContentPosterView(
                            show: show,
                            onClick: { id in presenter.dispatch(action: ShowClicked(id: show.tmdbId)) }
                        )
                    }
                }
                .edgesIgnoringSafeArea(.all)
                .frame(height: 450)
                .padding(.top, 70)
                
                
                CustomIndicator(shows)
                    .padding()
                    .padding(.top, 10)
            }
        }
    }
    
    @ViewBuilder
    func BackgroundView(_ tvShows: [DiscoverShow]?) -> some View {
        if let shows = tvShows {
            if !shows.isEmpty {
                GeometryReader { proxy in
                    
                    TabView(selection: $currentIndex) {
                        ForEach(shows.indices, id: \.self) { index in
                            TransparentImageBackground(imageUrl: shows[index].posterImageUrl)
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
    
    @ViewBuilder
    func CustomIndicator(_ shows: [DiscoverShow]) -> some View {
        HStack(spacing: 5) {
            ForEach(shows.indices, id: \.self) { index in
                Circle()
                    .fill(currentIndex == index ? Color.accent: .gray.opacity(0.5))
                    .frame(width: currentIndex == index ? 10 : 6, height: currentIndex == index ? 10 : 6)
            }
        }
        .animation(.easeInOut, value: currentIndex)
    }
    
}
