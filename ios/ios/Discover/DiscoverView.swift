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
            .toolbar {
                Button("Done") {  }
            }
            .navigationTitle("releaseDates")
        }
    }
    
    
    @ViewBuilder
    func DiscoverContent(presenter: DiscoverShowsPresenter) -> some View {
        ZStack {
            let contentState = uiState as! DataLoaded
            
            BackgroundView(tvShows: contentState.recommendedShows)
            
                ScrollView(.vertical, showsIndicators: false) {
                    VStack {
                        let state = contentState
                        
                        if(state.errorMessage != nil) {
                            FullScreenView(systemName: "exclamationmark.triangle", message: state.errorMessage!)
                        } else {
                            
                            //Featured Shows
                            FeaturedContentView(tvShows: state.recommendedShows)
                            
                            //Anticipated shows
                            ShowRow(
                                categoryName: "Anticipated",
                                shows: state.anticipatedShows,
                                onClick: { id in
                                    presenter.dispatch(action: ShowClicked(id: id))
                                }
                            )
                            
                            //Trending shows
                            ShowRow(
                                categoryName: "Trending",
                                shows: state.trendingShows,
                                onClick: { id in
                                    presenter.dispatch(action: ShowClicked(id: id))
                                }
                            )
                            
                            //Popular Shows
                            ShowRow(
                                categoryName: "Popular",
                                shows: state.popularShows,
                                onClick: { id in
                                    presenter.dispatch(action: ShowClicked(id: id))
                                }
                            )
                            
                        
                        }
                        
                    }
                }
         
        }
        .frame(maxWidth: UIScreen.main.bounds.width, maxHeight: UIScreen.main.bounds.height,  alignment: .center)
        
    }
    
    
    @ViewBuilder
    func FeaturedContentView(tvShows: [TvShow]?) -> some View {
        if let shows = tvShows {
            if !shows.isEmpty {
                SnapCarousel(spacing: 10, trailingSpace: 120,index: $currentIndex, items: shows) { post  in
                    
                    GeometryReader{ proxy in
                        
                        let size = proxy.size
                        
                        ShowPosterImage(
                            posterSize: .big,
                            imageUrl: post.posterImageUrl,
                            showTitle: post.title,
                            showId: post.traktId,
                            onClick: { presenter.dispatch(action: ShowClicked(id: post.traktId))  }
                        )
                        .cornerRadius(12)
                        .shadow(color: Color("shadow1"), radius: 4, x: 0, y: 4)
                        .transition(AnyTransition.slide)
                        .animation(.spring())
                    }
                }
                .edgesIgnoringSafeArea(.all)
                .frame(height: 450)
                .padding(.top, 90)
                
                
                CustomIndicator(shows: shows)
                    .padding()
                    .padding(.top, 10)
            }
        }
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
                            .frame(width: size.width + 350, height: size.height + 200)
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
    
}
