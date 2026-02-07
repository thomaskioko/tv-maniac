import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct DiscoverTab: View {
    @Theme private var theme

    private let presenter: DiscoverShowsPresenter
    @StateObject @KotlinStateFlow private var uiState: DiscoverViewState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var currentIndex: Int
    @State private var showNavigationBar = false
    @State private var selectedShow: SwiftShow?
    @State private var showGlass: Double = 0
    @State private var rotationAngle: Double = 0
    @State private var isDraggingCarousel: Bool = false
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
        ZStack(alignment: .bottom) {
            ParallaxView(
                imageHeight: ParallaxConstants.defaultImageHeight,
                collapsedImageHeight: ParallaxConstants.collapsedImageHeight,
                header: { _ in
                    ZStack(alignment: .bottom) {
                        headerContent(shows: uiState.featuredShowsSwift)
                        showInfoOverlay(uiState.featuredShowsSwift)
                    }
                },
                content: {
                    discoverListContent(state: state)
                },
                onScroll: { offset in
                    showGlass = ParallaxConstants.glassOpacity(from: offset)
                }
            )
        }
        .background(theme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .overlay(
            GlassToolbar(
                title: title,
                opacity: showGlass,
                isLoading: state.isRefreshing
            ),
            alignment: .top
        )
        .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass)
        .coordinateSpace(name: "scrollView")
        .edgesIgnoringSafeArea(.top)
        .onDisappear {
            selectedShow = nil
        }
    }

    // MARK: - Header Content

    @ViewBuilder
    private func headerContent(shows: [SwiftShow]) -> some View {
        if shows.isEmpty {
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle())
                .scaleEffect(1.5)
                .tint(theme.colors.accent)
        } else {
            CarouselView(
                items: shows,
                currentIndex: $currentIndex,
                onItemScrolled: { item in
                    selectedShow = item
                    store.savedIndex = currentIndex
                },
                onDraggingChanged: { isDragging in
                    isDraggingCarousel = isDragging
                }
            ) { index in
                CarouselItemView(item: shows[index])
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
                    posterHeight: scrollViewHeight,
                    processorHeight: CarouselConstants.fixedImageHeight
                )
                .onTapGesture {
                    presenter.dispatch(action: ShowClicked(traktId: item.traktId))
                }
            }
        }
    }

    private enum CarouselConstants {
        static let fixedImageHeight: CGFloat = ParallaxConstants.defaultImageHeight
    }

    @ViewBuilder
    private func showInfoOverlay(_ shows: [SwiftShow]) -> some View {
        VStack(alignment: .leading) {
            Text(selectedShow?.title ?? "")
                .textStyle(theme.typography.headlineLarge)
                .foregroundColor(theme.colors.onSurface)
                .lineLimit(1)
                .frame(maxWidth: .infinity, alignment: .center)

            if let overview = selectedShow?.overview {
                Text(overview)
                    .textStyle(theme.typography.bodyLarge)
                    .foregroundColor(theme.colors.onSurface.opacity(0.9))
                    .multilineTextAlignment(.leading)
                    .lineLimit(4)
            }

            customIndicator(shows)
                .padding(.top, theme.spacing.xSmall)
                .frame(maxWidth: .infinity, alignment: .center)
        }
        .padding(.horizontal)
        .padding(.bottom, theme.spacing.medium)
        .frame(maxWidth: .infinity, alignment: .leading)
        .allowsHitTesting(false)
        .background(
            LinearGradient(
                gradient: Gradient(
                    colors: [
                        theme.colors.background,
                        theme.colors.background,
                        theme.colors.background,
                        theme.colors.background.opacity(0.9),
                        theme.colors.background.opacity(0.7),
                        theme.colors.background.opacity(0.4),
                        .clear,
                    ]
                ),
                startPoint: .bottom,
                endPoint: .top
            )
            .frame(height: 280)
            .allowsHitTesting(false)
        )
    }

    @ViewBuilder
    func customIndicator(_ shows: [SwiftShow]) -> some View {
        ZStack {
            Color.clear
                .frame(height: 10)

            CircularIndicator(
                totalItems: shows.count,
                currentIndex: currentIndex,
                isDragging: isDraggingCarousel
            )
            .allowsHitTesting(false)
            .transaction { transaction in
                // Disable any inherited animations
                transaction.animation = nil
            }
        }
    }

    // MARK: - Discover List Content

    @ViewBuilder
    private func discoverListContent(state _: DiscoverViewState) -> some View {
        VStack {
            NextEpisodesSection(
                title: String(\.label_discover_up_next),
                episodes: uiState.nextEpisodesSwift,
                chevronStyle: .chevronOnly,
                markWatchedLabel: String(\.menu_mark_watched),
                unfollowShowLabel: String(\.menu_unfollow_show),
                openSeasonLabel: String(\.menu_open_season),
                onEpisodeClick: { showTraktId, episodeId in
                    presenter.dispatch(action: NextEpisodeClicked(showTraktId: showTraktId, episodeId: episodeId))
                },
                onMarkWatched: { episode in
                    presenter.dispatch(action: MarkNextEpisodeWatched(
                        showTraktId: episode.showTraktId,
                        episodeId: episode.episodeId,
                        seasonNumber: episode.seasonNumber,
                        episodeNumber: episode.episodeNumberValue
                    ))
                },
                onUnfollowShow: { episode in
                    presenter.dispatch(action: UnfollowShowFromUpNext(showTraktId: episode.showTraktId))
                },
                onOpenSeason: { episode in
                    presenter.dispatch(action: OpenSeasonFromUpNext(
                        showTraktId: episode.showTraktId,
                        seasonId: episode.seasonId,
                        seasonNumber: episode.seasonNumber
                    ))
                }
            )

            HorizontalItemListView(
                title: String(\.label_discover_trending_today),
                chevronStyle: .chevronOnly,
                items: uiState.trendingTodaySwift,
                onClick: { id in presenter.dispatch(action: ShowClicked(traktId: id)) },
                onMoreClicked: { presenter.dispatch(action: TrendingClicked()) }
            )

            HorizontalItemListView(
                title: String(\.label_discover_upcoming),
                chevronStyle: .chevronOnly,
                items: uiState.upcomingShowsSwift,
                onClick: { id in presenter.dispatch(action: ShowClicked(traktId: id)) },
                onMoreClicked: { presenter.dispatch(action: UpComingClicked()) }
            )

            HorizontalItemListView(
                title: String(\.label_discover_popular),
                chevronStyle: .chevronOnly,
                items: uiState.popularShowsSwift,
                onClick: { id in presenter.dispatch(action: ShowClicked(traktId: id)) },
                onMoreClicked: { presenter.dispatch(action: PopularClicked()) }
            )

            HorizontalItemListView(
                title: String(\.label_discover_top_rated),
                chevronStyle: .chevronOnly,
                items: uiState.topRatedShowsSwift,
                onClick: { id in presenter.dispatch(action: ShowClicked(traktId: id)) },
                onMoreClicked: { presenter.dispatch(action: TopRatedClicked()) }
            )
        }
        .padding(.top, theme.spacing.medium)
        .padding(.bottom, 90)
        .background(theme.colors.background)
        .offset(y: -10)
    }

    // MARK: - Empty View

    @ViewBuilder
    private var emptyView: some View {
        VStack {
            Image(systemName: "list.bullet.below.rectangle")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .foregroundColor(theme.colors.accent)
                .textStyle(theme.typography.displayMedium)
                .frame(width: 160, height: 180)

            Text(String(\.generic_empty_content))
                .textStyle(theme.typography.titleMedium)
                .padding(.top, theme.spacing.xSmall)

            Text(String(\.missing_api_key))
                .textStyle(theme.typography.bodySmall)
                .padding(.top, 1)
                .padding(.bottom, theme.spacing.medium)

            Button(action: {
                presenter.dispatch(action: RefreshData())
            }, label: {
                Text(String(\.button_error_retry))
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundColor(theme.colors.accent)
            })
            .buttonStyle(BorderlessButtonStyle())
            .padding(theme.spacing.medium)
            .background(
                RoundedRectangle(cornerRadius: theme.shapes.small)
                    .stroke(theme.colors.accent, lineWidth: 2)
                    .background(.clear)
                    .cornerRadius(2)
            )
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding([.trailing, .leading], theme.spacing.medium)
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
