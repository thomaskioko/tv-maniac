import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct DiscoverTab: View {
    private let presenter: DiscoverShowsPresenter
    @StateObject @KotlinStateFlow private var uiState: DiscoverViewState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var currentIndex: Int
    @State private var showNavigationBar = false
    @State private var selectedShow: SwiftShow?
    @State private var showGlass: Double = 0
    @State private var rotationAngle: Double = 0
    private let title = String(\.label_discover_title)

    init(presenter: DiscoverShowsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
        _currentIndex = State(initialValue: SettingsAppStorage.shared.savedIndex)
    }

    var body: some View {
        if uiState.isEmpty {
            emptyView
        } else if uiState.showError {
            FullScreenView(
                systemName: "exclamationmark.arrow.triangle.2.circlepath",
                message: uiState.message?.message ?? String(\.generic_error_message)
            )
        } else {
            discoverLoadedContent(state: uiState)
        }
    }

    // MARK: - Discover Content

    @ViewBuilder
    private func discoverLoadedContent(state: DiscoverViewState) -> some View {
        ParallaxView(
            imageHeight: 550,
            collapsedImageHeight: 120,
            header: { _ in
                ZStack(alignment: .bottom) {
                    headerContent(shows: state.featuredShows)
                    showInfoOverlay(state.featuredShows.map {
                        $0.toSwift()
                    })
                }
            },
            content: {
                discoverListContent(state: state)
            },
            onScroll: { offset in
                let opacity = -offset - 150
                let normalizedOpacity = opacity / 200
                showGlass = max(0, min(1, normalizedOpacity))
            }
        )
        .background(Color.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .overlay(
            GlassToolbar(title: title, opacity: showGlass),
            alignment: .top
        )
        .animation(.easeInOut(duration: 0.25), value: showGlass)
        .coordinateSpace(name: CoordinateSpaces.scrollView)
        .edgesIgnoringSafeArea(.top)
    }

    // MARK: - Header Content

    @ViewBuilder
    private func headerContent(shows: [DiscoverShow]) -> some View {
        if shows.isEmpty {
            LoadingIndicatorView()
        } else {
            let items = shows.map {
                $0.toSwift()
            }
            ZStack(alignment: .top) {
                CarouselView(
                    items: items,
                    currentIndex: $currentIndex,
                    onItemScrolled: { item in
                        selectedShow = item
                        store.savedIndex = currentIndex
                    },
                    onItemTapped: { id in
                        presenter.dispatch(action: ShowClicked(id: id))
                    }
                ) { index in
                    CarouselItemView(item: items[index])
                }
                headerNavigationBar(shows: items)
            }
        }
    }

    @ViewBuilder
    private func CarouselItemView(item: SwiftShow) -> some View {
        GeometryReader { geometry in
            let scrollViewHeight = geometry.size.height

            ZStack(alignment: .bottom) {
                PosterItemView(
                    title: item.title,
                    posterUrl: item.posterUrl,
                    posterWidth: geometry.size.width,
                    posterHeight: scrollViewHeight
                )
                    .onTapGesture {
                        presenter.dispatch(action: ShowClicked(id: item.tmdbId))
                    }
            }
        }
    }

    // MARK: - Navigation Bar

    @ViewBuilder
    private func headerNavigationBar(shows: [SwiftShow]) -> some View {
        let show = getShow(currentIndex: currentIndex, shows: shows)
        HStack {
            Text(title)
                .font(.largeTitle)
                .fontWeight(.bold)

            Spacer()

            HStack(spacing: 8) {
                Button(
                    action: {
                        // TODO: Invoke account navigation action.
                    }
                ) {
                    Image(systemName: "person")
                        .font(.avenirNext(size: 17))
                        .foregroundColor(.white)
                        .frame(width: 32, height: 32)
                        .padding(2)
                        .background(Color.black.opacity(0.3))
                        .clipShape(Circle())
                }

                Button(
                    action: { presenter.dispatch(action: RefreshData()) }
                ) {
                    if uiState.isRefreshing {
                        Image(systemName: "arrow.clockwise")
                            .font(.avenirNext(size: 17))
                            .foregroundColor(.white)
                            .frame(width: 32, height: 32)
                            .padding(2)
                            .background(Color.black.opacity(0.3))
                            .clipShape(Circle())
                            .rotationEffect(.degrees(rotationAngle))
                            .onAppear {
                                withAnimation(.linear(duration: 1.0).repeatForever(autoreverses: false)) {
                                    rotationAngle = 360
                                }
                            }
                    } else {
                        Image(systemName: "arrow.clockwise")
                            .font(.avenirNext(size: 17))
                            .foregroundColor(.white)
                            .frame(width: 32, height: 32)
                            .padding(2)
                            .background(Color.black.opacity(0.3))
                            .clipShape(Circle())
                    }
                }
            }
        }
        .padding(.horizontal)
        .padding(.top, 70)
        .padding(.bottom, 8)
        .foregroundColor(.white)
    }

    @ViewBuilder
    private func showInfoOverlay(_ shows: [SwiftShow]) -> some View {
        VStack(alignment: .leading) {
            Text(selectedShow?.title ?? "")
                .font(.system(size: 46, weight: .bold))
                .foregroundColor(.white)
                .lineLimit(1)
                .frame(maxWidth: .infinity, alignment: .center)

            if let overview = selectedShow?.overview {
                Text(overview)
                    .font(.avenirNext(size: 17))
                    .foregroundColor(.white)
                    .multilineTextAlignment(.leading)
                    .lineLimit(2)
            }

            customIndicator(shows)
                .padding(.top, 8)
                .frame(maxWidth: .infinity, alignment: .center)
        }
        .padding(.horizontal)
        .padding(.bottom, 20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            LinearGradient(
                gradient: Gradient(
                    colors: [
                        .black,
                        .black.opacity(0.8),
                        .black.opacity(0.6),
                        .black.opacity(0.4),
                        .clear,
                    ]
                ),
                startPoint: .bottom,
                endPoint: .top
            )
                .frame(height: 550)
                .allowsHitTesting(false)
        )
    }

    @ViewBuilder
    private func customIndicator(_ shows: [SwiftShow]) -> some View {
        HStack(spacing: 8) {
            ForEach(0..<shows.count, id: \.self) { index in
                Circle()
                    .fill(index == currentIndex ? Color.white : Color.white.opacity(0.3))
                    .frame(width: 8, height: 8)
                    .scaleEffect(index == currentIndex ? 1.2 : 1.0)
                    .animation(.spring(), value: currentIndex)
            }
        }
    }

    // MARK: - Discover List Content

    @ViewBuilder
    private func discoverListContent(state: DiscoverViewState) -> some View {
        VStack(alignment: .leading, spacing: 16) {
            if !state.trendingShows.isEmpty {
                HorizontalShowContentView(
                    title: String(\.label_trending_today),
                    chevronStyle: .chevron,
                    items: state.trendingShows.map {
                        $0.toSwift()
                    },
                    onClick: { id in
                        presenter.dispatch(action: ShowClicked(id: id))
                    },
                    onMoreClicked: {
                        presenter.dispatch(action: TrendingShowsClicked())
                    }
                )
            }

            if !state.popularShows.isEmpty {
                HorizontalShowContentView(
                    title: String(\.label_popular),
                    chevronStyle: .chevron,
                    items: state.popularShows.map {
                        $0.toSwift()
                    },
                    onClick: { id in
                        presenter.dispatch(action: ShowClicked(id: id))
                    },
                    onMoreClicked: {
                        presenter.dispatch(action: PopularShowsClicked())
                    }
                )
            }

            if !state.topRatedShows.isEmpty {
                HorizontalShowContentView(
                    title: String(\.label_top_rated),
                    chevronStyle: .chevron,
                    items: state.topRatedShows.map {
                        $0.toSwift()
                    },
                    onClick: { id in
                        presenter.dispatch(action: ShowClicked(id: id))
                    },
                    onMoreClicked: {
                        presenter.dispatch(action: TopRatedShowsClicked())
                    }
                )
            }

            if !state.onTheAirShows.isEmpty {
                HorizontalShowContentView(
                    title: String(\.label_on_the_air),
                    chevronStyle: .chevron,
                    items: state.onTheAirShows.map {
                        $0.toSwift()
                    },
                    onClick: { id in
                        presenter.dispatch(action: ShowClicked(id: id))
                    },
                    onMoreClicked: {
                        presenter.dispatch(action: OnTheAirShowsClicked())
                    }
                )
            }

            if !state.airingTodayShows.isEmpty {
                HorizontalShowContentView(
                    title: String(\.label_airing_today),
                    chevronStyle: .chevron,
                    items: state.airingTodayShows.map {
                        $0.toSwift()
                    },
                    onClick: { id in
                        presenter.dispatch(action: ShowClicked(id: id))
                    },
                    onMoreClicked: {
                        presenter.dispatch(action: AiringTodayShowsClicked())
                    }
                )
            }
        }
        .padding(.bottom, 16)
    }

    // MARK: - Empty View

    private var emptyView: some View {
        FullScreenView(
            systemName: "tv",
            message: String(\.empty_discover_message)
        )
    }

    // MARK: - Helper Methods

    private func getShow(currentIndex: Int, shows: [SwiftShow]) -> SwiftShow? {
        guard !shows.isEmpty, currentIndex < shows.count else {
            return nil
        }
        return shows[currentIndex]
    }
}
