import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct DiscoverTab: View {
    @Theme private var theme

    private let presenter: DiscoverShowsPresenter
    @StateObject @KotlinStateFlow private var uiState: DiscoverViewState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var currentIndex: Int
    @State private var selectedShow: SwiftShow?
    @State private var showGlass: Double = 0
    @State private var isDraggingCarousel: Bool = false
    @State private var pullOffset: CGFloat = 0
    @State private var isRefreshing: Bool = false
    @State private var isScrollInteracting: Bool = false
    @State private var toast: Toast?
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
    private func discoverLoadedContent(state _: DiscoverViewState) -> some View {
        ZStack(alignment: .top) {
            discoverScrollView

            LinearGradient(
                colors: [
                    .black.opacity(0.6),
                    .black.opacity(0.3),
                    .clear,
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 150)
            .allowsHitTesting(false)

            if #available(iOS 18.0, *) {
                let progress = min(pullOffset / RefreshConstants.threshold, 1.0)

                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    .scaleEffect(2.0)
                    .opacity(pullOffset > 0 ? max(0.6, Double(progress)) : 0)
                    .padding(.top, RefreshConstants.indicatorTopPadding)
            }

            GlassToolbar(
                title: title,
                opacity: showGlass,
                isLoading: false,
                trailingIcon: {
                    profileIcon(avatarUrl: uiState.userAvatarUrl)
                }
            )
            .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass)
        }
        .background(theme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .edgesIgnoringSafeArea(.top)
        .onDisappear {
            selectedShow = nil
        }
        .onChange(of: uiState.isRefreshing) { _, newValue in
            if !newValue, isRefreshing {
                withAnimation(.easeOut(duration: AnimationConstants.defaultDuration)) {
                    isRefreshing = false
                }
            }
        }
        .onChange(of: uiState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(
                    type: .error,
                    title: "Error",
                    message: message.message
                )
                presenter.dispatch(action: MessageShown(id: message.id))
            }
        }
        .toastView(toast: $toast)
    }

    @ViewBuilder
    private var discoverScrollView: some View {
        if #available(iOS 18.0, *) {
            scrollViewContent
                .onScrollGeometryChange(for: CGFloat.self) { geometry in
                    geometry.contentOffset.y
                } action: { _, newValue in
                    if !isRefreshing, isScrollInteracting {
                        pullOffset = max(0, -newValue)
                    }
                }
                .onScrollPhaseChange { oldPhase, newPhase in
                    isScrollInteracting = newPhase == .interacting
                    if oldPhase == .interacting {
                        if !isRefreshing, pullOffset >= RefreshConstants.threshold {
                            triggerRefresh()
                        }
                        pullOffset = 0
                    }
                }
        } else {
            scrollViewContent
        }
    }

    private var scrollViewContent: some View {
        ScrollView(showsIndicators: false) {
            VStack(spacing: 0) {
                GeometryReader { proxy in
                    let scrollY = proxy.frame(in: .named("discoverScroll")).minY

                    headerContent(shows: uiState.featuredShowsSwift)
                        .frame(width: proxy.size.width, height: CarouselConstants.headerHeight + max(scrollY, 0))
                        .offset(y: -max(scrollY, 0))
                        .overlay(alignment: .bottom) {
                            showInfoOverlay(uiState.featuredShowsSwift)
                        }
                        .onChange(of: scrollY) { _, newValue in
                            DispatchQueue.main.async {
                                showGlass = newValue < 0
                                    ? ParallaxConstants.glassOpacity(from: newValue)
                                    : 0
                            }
                        }
                }
                .frame(height: CarouselConstants.headerHeight)

                discoverListContent(state: uiState)
            }
        }
        .coordinateSpace(name: "discoverScroll")
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
                carouselItemView(item: shows[index])
            }
        }
    }

    @ViewBuilder
    private func carouselItemView(item: SwiftShow) -> some View {
        GeometryReader { geometry in
            PosterItemView(
                title: item.title,
                posterUrl: item.posterUrl,
                imageType: .backdrop,
                posterWidth: geometry.size.width,
                posterHeight: geometry.size.height,
                processorHeight: CarouselConstants.fixedImageHeight
            )
            .onTapGesture {
                presenter.dispatch(action: ShowClicked(traktId: item.traktId))
            }
        }
    }

    private enum CarouselConstants {
        static let headerHeight: CGFloat = 580
        static let fixedImageHeight: CGFloat = headerHeight
    }

    private enum RefreshConstants {
        static let threshold: CGFloat = 80
        static let indicatorTopPadding: CGFloat = 100
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
                stops: [
                    .init(color: theme.colors.background, location: 0),
                    .init(color: theme.colors.background, location: 0.3),
                    .init(color: theme.colors.background.opacity(0.9), location: 0.5),
                    .init(color: theme.colors.background.opacity(0.7), location: 0.65),
                    .init(color: theme.colors.background.opacity(0.4), location: 0.8),
                    .init(color: .clear, location: 1.0),
                ],
                startPoint: .bottom,
                endPoint: .top
            )
            .padding(.top, -120)
            .allowsHitTesting(false)
        )
    }

    @ViewBuilder
    private func customIndicator(_ shows: [SwiftShow]) -> some View {
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
        .background(theme.colors.background)
        .offset(y: -10)
    }

    private func triggerRefresh() {
        isRefreshing = true
        presenter.dispatch(action: RefreshData())
    }

    // MARK: - Top Bar Icons

    @ViewBuilder
    private func profileIcon(avatarUrl: String?) -> some View {
        GlassButton(action: {
            presenter.dispatch(action: ProfileIconClicked())
        }) {
            AvatarView(
                avatarUrl: avatarUrl,
                size: 32
            )
        }
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
}
