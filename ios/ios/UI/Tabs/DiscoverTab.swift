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
        ScrollView(showsIndicators: false) {
            VStack(spacing: 0) {
                PullToRefreshView(
                    coordinateSpaceName: "scrollView"
                ) {
                    await MainActor.run {
                        presenter.dispatch(action: RefreshData())
                    }
                }

                ZStack(alignment: .bottom) {
                    headerContent(shows: state.featuredShows)
                    showInfoOverlay(state.featuredShows.map {
                        $0.toSwift()
                    })
                }
                .frame(height: 550)

                discoverListContent(state: state)
            }
            .background(
                GeometryReader { geometry in
                    Color.clear
                        .onChange(of: geometry.frame(in: .named("scrollView")).minY) { offset in
                            let opacity = -offset - 150
                            let normalizedOpacity = opacity / 200
                            showGlass = max(0, min(1, normalizedOpacity))
                        }
                }
            )
        }
        .background(Color.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .overlay(
            GlassToolbar(title: title, opacity: showGlass),
            alignment: .top
        )
        .animation(.easeInOut(duration: 0.25), value: showGlass)
        .coordinateSpace(name: "scrollView")
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

                if uiState.isRefreshing {
                    Button(
                        action: { presenter.dispatch(action: RefreshData()) }
                    ) {
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
    func customIndicator(_ shows: [SwiftShow]) -> some View {
        HStack(spacing: 5) {
            ForEach(shows.indices, id: \.self) { index in
                Circle()
                    .fill(currentIndex == index ? .white : .gray.opacity(0.5))
                    .frame(width: currentIndex == index ? 10 : 6, height: currentIndex == index ? 10 : 6)
            }
        }
        .animation(.easeInOut, value: currentIndex)
    }

    // MARK: - Discover List Content

    @ViewBuilder
    private func discoverListContent(state: DiscoverViewState) -> some View {
        VStack {
            HorizontalItemListView(
                title: String(\.label_discover_upcoming),
                chevronStyle: .chevronOnly,
                items: state.upcomingShows.map {
                    $0.toSwift()
                },
                onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
                onMoreClicked: { presenter.dispatch(action: UpComingClicked()) }
            )

            HorizontalItemListView(
                title: String(\.label_discover_trending_today),
                chevronStyle: .chevronOnly,
                items: state.trendingToday.map {
                    $0.toSwift()
                },
                onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
                onMoreClicked: { presenter.dispatch(action: TrendingClicked()) }
            )

            HorizontalItemListView(
                title: String(\.label_discover_popular),
                chevronStyle: .chevronOnly,
                items: state.popularShows.map {
                    $0.toSwift()
                },
                onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
                onMoreClicked: { presenter.dispatch(action: PopularClicked()) }
            )

            HorizontalItemListView(
                title: String(\.label_discover_top_rated),
                chevronStyle: .chevronOnly,
                items: state.topRatedShows.map {
                    $0.toSwift()
                },
                onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
                onMoreClicked: { presenter.dispatch(action: TopRatedClicked()) }
            )
        }
        .padding(.top, 16)
        .padding(.bottom, 90)
        .background(Color.background)
        .offset(y: -10)
    }

    // MARK: - Empty View

    @ViewBuilder
    private var emptyView: some View {
        VStack {
            Image(systemName: "list.bullet.below.rectangle")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .foregroundColor(Color.accent)
                .font(Font.title.weight(.thin))
                .frame(width: 160, height: 180)

            Text(String(\.generic_empty_content))
                .titleSemiBoldFont(size: 18)
                .padding(.top, 8)

            Text(String(\.missing_api_key))
                .captionFont(size: 16)
                .padding(.top, 1)
                .padding(.bottom, 16)

            Button(action: {
                presenter.dispatch(action: RefreshData())
            }, label: {
                Text(String(\.button_error_retry))
                    .bodyMediumFont(size: 16)
                    .foregroundColor(Color.accent)
            })
            .buttonStyle(BorderlessButtonStyle())
            .padding(16)
            .background(
                RoundedRectangle(cornerRadius: 5)
                    .stroke(Color.accent, lineWidth: 2)
                    .background(.clear)
                    .cornerRadius(2)
            )
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding([.trailing, .leading], 16)
    }

    private func getShow(currentIndex: Int, shows: [SwiftShow]) -> SwiftShow? {
        guard !shows.isEmpty, currentIndex < shows.count else {
            return nil
        }
        return shows[currentIndex]
    }

    private enum CoordinateSpaces {
        case scrollView
    }
}

struct PullToRefreshView: View {
    var coordinateSpaceName: String
    var onRefresh: () async -> Void
    @State private var needRefresh: Bool = false

    var body: some View {
        GeometryReader { geo in
            if geo.frame(in: .named(coordinateSpaceName)).midY > 50 {
                Spacer()
                    .onAppear {
                        needRefresh = true
                    }
            } else if geo.frame(in: .named(coordinateSpaceName)).midY < 1 {
                Spacer()
                    .onAppear {
                        if needRefresh {
                            needRefresh = false
                            Task {
                                await onRefresh()
                            }
                        }
                    }
            }
            HStack {
                Spacer()
                if needRefresh {
                    ThemedProgressView()
                }
                Spacer()
            }
        }
        .padding(.top, -50)
    }
}
