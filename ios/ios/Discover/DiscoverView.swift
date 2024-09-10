import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacUI

struct DiscoverView: View {
    @Environment(\.colorScheme) var scheme
    @State private var currentIndex: Int = 2
    @StateFlow private var uiState: DiscoverState

    private let component: DiscoverShowsComponent

    init(component: DiscoverShowsComponent) {
        self.component = component
        _uiState = StateFlow(component.state)
    }

    var body: some View {
        VStack {
            switch onEnum(of: uiState) {
                case .loading:
                    LoadingIndicatorView()
                        .frame(maxWidth: UIScreen.main.bounds.width, maxHeight: UIScreen.main.bounds.height, alignment: .center)
                case .dataLoaded(let state):
                    LoadedContent(state: state)
                case .emptyState:
                    emptyView
                case .errorState(let error):
                    FullScreenView(
                        systemName: "exclamationmark.arrow.triangle.2.circlepath",
                        message: error.errorMessage ?? "Something went wrong!!"
                    )
            }
        }
    }

    @ViewBuilder
    private func LoadedContent(state: DataLoaded) -> some View {
        ZStack {
            BackgroundView(state.featuredShows)

            ScrollableContent(state: state)
                .refreshable {
                    component.dispatch(action: RefreshData())
                }
        }
    }

    @ViewBuilder
    func ScrollableContent(state: DataLoaded) -> some View {
        ScrollView(showsIndicators: false) {
            if state.errorMessage != nil {
                FullScreenView(systemName: "exclamationmark.triangle", message: state.errorMessage!)
            } else {
                FeaturedContentView(state.featuredShows)

                HorizontalItemListView(
                    title: "Upcoming",
                    chevronStyle: .chevronOnly,
                    items: state.upcomingShows.map { $0.toSwift() },
                    onClick: { id in component.dispatch(action: ShowClicked(id: id)) },
                    onMoreClicked: { component.dispatch(action: UpComingClicked()) }
                )

                HorizontalItemListView(
                    title: "Trending Today",
                    chevronStyle: .chevronOnly,
                    items: state.trendingToday.map { $0.toSwift() },
                    onClick: { id in component.dispatch(action: ShowClicked(id: id)) },
                    onMoreClicked: { component.dispatch(action: TrendingClicked()) }
                )

                HorizontalItemListView(
                    title: "Popular",
                    chevronStyle: .chevronOnly,
                    items: state.popularShows.map { $0.toSwift() },
                    onClick: { id in component.dispatch(action: ShowClicked(id: id)) },
                    onMoreClicked: { component.dispatch(action: PopularClicked()) }
                )

                HorizontalItemListView(
                    title: "Top Rated",
                    chevronStyle: .chevronOnly,
                    items: state.topRatedShows.map { $0.toSwift() },
                    onClick: { id in component.dispatch(action: ShowClicked(id: id)) },
                    onMoreClicked: { component.dispatch(action: TopRatedClicked()) }
                )
            }
        }
    }

    @ViewBuilder
    func FeaturedContentView(_ shows: [DiscoverShow]?) -> some View {
        if let shows = shows {
            if !shows.isEmpty {
                SnapCarousel(
                    spacing: 10,
                    trailingSpace: 120,
                    index: $currentIndex,
                    items: shows.map { $0.toSwift() }
                ) { show in
                    GeometryReader { _ in
                        FeaturedContentPosterView(
                            showId: show.tmdbId,
                            title: show.title,
                            posterImageUrl: show.posterUrl,
                            isInLibrary: show.inLibrary,
                            onClick: { id in component.dispatch(action: ShowClicked(id: show.tmdbId)) }
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
                GeometryReader { _ in

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
                    .fill(currentIndex == index ? Color.accent : .gray.opacity(0.5))
                    .frame(width: currentIndex == index ? 10 : 6, height: currentIndex == index ? 10 : 6)
            }
        }
        .animation(.easeInOut, value: currentIndex)
    }

    @ViewBuilder
    private var emptyView: some View {
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
                component.dispatch(action: ReloadData())
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
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding([.trailing, .leading], 16)
    }
}

extension DiscoverShow {
    func toSwift() -> SwiftShow {
        .init(
            tmdbId: tmdbId,
            title: title,
            posterUrl: posterImageUrl,
            backdropUrl: nil,
            inLibrary: inLibrary
        )
    }
}
