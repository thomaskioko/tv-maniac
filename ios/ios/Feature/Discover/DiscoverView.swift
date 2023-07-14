import SwiftUI
import TvManiac
import os.log

struct DiscoverView: View {

    @ObservedObject var viewModel: DiscoverShowsViewModel = DiscoverShowsViewModel()

    @Namespace var animation
    @Environment(\.colorScheme) var scheme
    
    @State var currentIndex: Int = 2
    @State var size: CGSize = CGSize(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
    @State private var show: Bool = false
    @State private var selectedRow: Int64 = -1
    @State private var regularSheet: Bool = false
    
    var body: some View {
        VStack {
            switch viewModel.showState {
            case is Loading:
                LoadingIndicatorView()
                    .frame(maxWidth: UIScreen.main.bounds.width, maxHeight: UIScreen.main.bounds.height,  alignment: .center)
            case is DataLoaded:
                let state = viewModel.showState as! DataLoaded
                DiscoverContent(contentState: state)
            default:
                fatalError("Unhandled case: \(viewModel.showState)")
            }
        }
        .background(Color.background)
        .onAppear { viewModel.startStateMachine() } }
    
    
    @ViewBuilder
    func DiscoverContent(contentState: DiscoverState) -> some View {
        NavigationStack {
            
            ZStack {
                BackgroundView(contentState: contentState)
                
                GeometryReader { geometry in
                    ScrollView(.vertical, showsIndicators: false) {
                        VStack {
                            switch contentState {
                            case is DataLoaded:
                                let state = contentState as! DataLoaded
                                
                                if(state.isContentEmpty && state.errorMessage != nil){
                                    FullScreenView(systemName: "exclamationmark.triangle", message: state.errorMessage!)
                                } else if(state.isContentEmpty){
                                    FullScreenView(systemName: "list.and.film", message: "Looks like your stash is empty")
                                } else if(!state.isContentEmpty && state.errorMessage != nil){
                                    //TODO:: Show Toast
                                } else {
                                    
                                    //Featured Shows
                                    FeaturedContentView(tvShows: state.recommendedShows)
                                    
                                    //Anticipated shows
                                    ShowRow(
                                        categoryName: "Anticipated",
                                        shows: state.anticipatedShows
                                    )
                                    
                                    //Trending shows
                                    ShowRow(
                                        categoryName: "Trending",
                                        shows: state.trendingShows
                                    )
                                    
                                    //Popular Shows
                                    ShowRow(
                                        categoryName: "Popular",
                                        shows: state.popularShows
                                    )
                                    
                                    Spacer()
                                }
                            default:
                                let _ = print("Unhandled case: \(contentState)")
                            }
                        }
                        .frame(width: geometry.size.width)      // Make the scroll view full-width
                        .frame(minHeight: geometry.size.height)
                    }
                }
            }
        }
    }
    
    
    @ViewBuilder
    func FeaturedContentView(tvShows: [TvShow]?) -> some View {
        if let shows = tvShows {
            if !shows.isEmpty {
                /**
                 * This is a temporary implementation for navigation to the detail view. The problem is NavigationView
                 * does not support MatchedGeometry effect. The other alternative would be to use an overlay
                 * for the detailView.
                 */
                NavigationLink(destination: ShowDetailView(showId: (shows[currentIndex].traktId))) {
                    SnapCarousel(spacing: 10, trailingSpace: 70, index: $currentIndex, items: shows) { show in
                        
                        GeometryReader { proxy in
                            let size = proxy.size
                            
                            ShowPosterImage(
                                posterSize: .big,
                                imageUrl: show.posterImageUrl,
                                showTitle: show.title
                            )
                            .frame(width: size.width, height: size.height)
                            .matchedGeometryEffect(id: show.traktId, in: animation)
                        }
                    }
                }
                .frame(height: 450)
                .padding(.top, 90)
                
                CustomIndicator(shows: shows)
            }
        }
    }
    
    @ViewBuilder
    func BackgroundView(contentState: DiscoverState) -> some View {
        if contentState is DataLoaded {
            let state = contentState as! DataLoaded
            
            if let tvShows = state.recommendedShows {
                if !tvShows.isEmpty {
                    GeometryReader { proxy in
                        let size = proxy.size
                        
                        TabView(selection: $currentIndex) {
                            ForEach(tvShows.indices, id: \.self) { index in
                                ShowPosterImage(
                                    posterSize: .big,
                                    imageUrl: tvShows[index].posterImageUrl,
                                    showTitle: tvShows[index].title
                                )
                                .aspectRatio(contentMode: .fill)
                                .frame(width: size.width, height: size.height)
                                
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

struct DiscoverView_Previews: PreviewProvider {
    static var previews: some View {
        DiscoverView()
        
        DiscoverView()
            .preferredColorScheme(.dark)
    }
}
