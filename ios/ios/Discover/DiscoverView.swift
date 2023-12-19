import SwiftUI
import TvManiac
import os.log

struct DiscoverView: View {
    
    @Environment(\.colorScheme) var scheme
    
    @State var currentIndex: Int = 2
    
    private let presenter: DiscoverShowsPresenter
    
    @StateValue
    private var uiState: DiscoverState
    
    init(presenter: DiscoverShowsPresenter){
        self.presenter = presenter
        _uiState = StateValue(presenter.state)
    }
    
    var body: some View {
        NavigationStack {
            VStack {
                switch uiState {
                case is Loading:
                    LoadingIndicatorView()
                        .frame(maxWidth: UIScreen.main.bounds.width, maxHeight: UIScreen.main.bounds.height,  alignment: .center)
                case is DataLoaded: DiscoverContent(presenter: presenter)
                default:
                    fatalError("Unhandled case: \(uiState)")
                }
            }
            .background(Color.background)
            .toolbar {}
            .navigationTitle("")
        }
    }
    
    
    @ViewBuilder
    func DiscoverContent(presenter: DiscoverShowsPresenter) -> some View {
        ZStack {
            let contentState = uiState as! DataLoaded
            
            BackgroundView(tvShows: contentState.featuredShows)
            
                ScrollView(.vertical, showsIndicators: false) {
                    VStack {
                        let state = contentState
                        
                        if(state.errorMessage != nil) {
                            FullScreenView(systemName: "exclamationmark.triangle", message: state.errorMessage!)
                        } else {
                            
                            //Featured Shows
                            FeaturedContentView(state.featuredShows)
                            
                            HorizontalItemContentListView(
                                items: state.upcomingShows,
                                title: "Upcoming",
                                onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) }
                            )
                            
                            //Trending Today
                            HorizontalItemContentListView(
                                items: state.trendingToday,
                                title: "Trending Today",
                                onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) }
                            )
                            
                            //Popular Shows
                            HorizontalItemContentListView(
                                items: state.popularShows,
                                title: "Popular",
                                onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) }
                            )
                            
                            //Top Rated Shows
                            HorizontalItemContentListView(
                                items: state.topRatedShows,
                                title: "Top Rated",
                                onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) }
                            )
                        }
                        
                        Spacer()
                    }
                }
         
        }
        .frame(maxWidth: UIScreen.main.bounds.width, maxHeight: UIScreen.main.bounds.height,  alignment: .center)
        .padding(.bottom, 64)
        
    }
    
    
    @ViewBuilder
    func FeaturedContentView(_ shows: [DiscoverShow]?) -> some View {
        if let shows = shows {
            if !shows.isEmpty {
                SnapCarousel(spacing: 10, trailingSpace: 120, index: $currentIndex, items: shows) { show  in
                    
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
    func BackgroundView(tvShows: [DiscoverShow]?) -> some View {
        if let shows = tvShows {
            if !shows.isEmpty {
                GeometryReader { proxy in
                    
                    TabView(selection: $currentIndex) {
                        ForEach(shows.indices, id: \.self) { index in
                            FeaturedContentPosterView(
                                show: shows[index],
                                onClick: { id in },
                                posterWidth: CGFloat(320)
                            )
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
                    .fill(currentIndex == index ? Color.accent_color : .gray.opacity(0.5))
                    .frame(width: currentIndex == index ? 10 : 6, height: currentIndex == index ? 10 : 6)
            }
        }
        .animation(.easeInOut, value: currentIndex)
    }
    
}
