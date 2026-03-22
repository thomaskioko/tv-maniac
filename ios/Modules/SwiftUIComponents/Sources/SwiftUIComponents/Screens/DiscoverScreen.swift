import SwiftUI

public struct DiscoverScreen: View {
    @Theme private var appTheme

    private let title: String
    private let isEmpty: Bool
    private let showError: Bool
    private let errorMessage: String?
    private let featuredShows: [SwiftShow]
    private let nextEpisodes: [SwiftNextEpisode]
    private let trendingToday: [SwiftShow]
    private let upcomingShows: [SwiftShow]
    private let popularShows: [SwiftShow]
    private let topRatedShows: [SwiftShow]
    private let isRefreshing: Bool
    private let emptyContentText: String
    private let missingApiKeyText: String
    private let retryText: String
    private let upNextTitle: String
    private let trendingTitle: String
    private let upcomingTitle: String
    private let popularTitle: String
    private let topRatedTitle: String
    @Binding private var currentIndex: Int
    @Binding private var toast: Toast?
    @Binding private var selectedEpisode: SwiftNextEpisode?
    private let onShowClicked: (Int64) -> Void
    private let onSearchClicked: () -> Void
    private let onRefresh: () -> Void
    private let onTrendingClicked: () -> Void
    private let onUpcomingClicked: () -> Void
    private let onPopularClicked: () -> Void
    private let onTopRatedClicked: () -> Void
    private let onNextEpisodeClicked: (SwiftNextEpisode) -> Void
    private let onNextEpisodeLongPress: (SwiftNextEpisode) -> Void
    private let onCarouselIndexChanged: (Int) -> Void
    private let episodeSheetContent: ((SwiftNextEpisode) -> AnyView)?

    public init(
        title: String,
        isEmpty: Bool,
        showError: Bool,
        errorMessage: String?,
        featuredShows: [SwiftShow],
        nextEpisodes: [SwiftNextEpisode],
        trendingToday: [SwiftShow],
        upcomingShows: [SwiftShow],
        popularShows: [SwiftShow],
        topRatedShows: [SwiftShow],
        isRefreshing: Bool,
        emptyContentText: String,
        missingApiKeyText: String,
        retryText: String,
        upNextTitle: String,
        trendingTitle: String,
        upcomingTitle: String,
        popularTitle: String,
        topRatedTitle: String,
        currentIndex: Binding<Int>,
        toast: Binding<Toast?>,
        selectedEpisode: Binding<SwiftNextEpisode?>,
        onShowClicked: @escaping (Int64) -> Void,
        onSearchClicked: @escaping () -> Void,
        onRefresh: @escaping () -> Void,
        onTrendingClicked: @escaping () -> Void,
        onUpcomingClicked: @escaping () -> Void,
        onPopularClicked: @escaping () -> Void,
        onTopRatedClicked: @escaping () -> Void,
        onNextEpisodeClicked: @escaping (SwiftNextEpisode) -> Void,
        onNextEpisodeLongPress: @escaping (SwiftNextEpisode) -> Void,
        onCarouselIndexChanged: @escaping (Int) -> Void,
        episodeSheetContent: ((SwiftNextEpisode) -> AnyView)? = nil
    ) {
        self.title = title
        self.isEmpty = isEmpty
        self.showError = showError
        self.errorMessage = errorMessage
        self.featuredShows = featuredShows
        self.nextEpisodes = nextEpisodes
        self.trendingToday = trendingToday
        self.upcomingShows = upcomingShows
        self.popularShows = popularShows
        self.topRatedShows = topRatedShows
        self.isRefreshing = isRefreshing
        self.emptyContentText = emptyContentText
        self.missingApiKeyText = missingApiKeyText
        self.retryText = retryText
        self.upNextTitle = upNextTitle
        self.trendingTitle = trendingTitle
        self.upcomingTitle = upcomingTitle
        self.popularTitle = popularTitle
        self.topRatedTitle = topRatedTitle
        _currentIndex = currentIndex
        _toast = toast
        _selectedEpisode = selectedEpisode
        self.onShowClicked = onShowClicked
        self.onSearchClicked = onSearchClicked
        self.onRefresh = onRefresh
        self.onTrendingClicked = onTrendingClicked
        self.onUpcomingClicked = onUpcomingClicked
        self.onPopularClicked = onPopularClicked
        self.onTopRatedClicked = onTopRatedClicked
        self.onNextEpisodeClicked = onNextEpisodeClicked
        self.onNextEpisodeLongPress = onNextEpisodeLongPress
        self.onCarouselIndexChanged = onCarouselIndexChanged
        self.episodeSheetContent = episodeSheetContent
    }

    @State private var selectedShow: SwiftShow?
    @State private var showGlass: Double = 0
    @State private var isDraggingCarousel: Bool = false
    @State private var pullOffset: CGFloat = 0
    @State private var isRefreshingLocal: Bool = false
    @State private var isScrollInteracting: Bool = false

    public var body: some View {
        if isEmpty {
            emptyView
        } else if showError {
            EmptyStateView(
                systemName: "exclamationmark.arrow.triangle.2.circlepath",
                title: errorMessage ?? "Something went wrong"
            )
        } else {
            discoverLoadedContent
        }
    }

    private var discoverLoadedContent: some View {
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
                    GlassButton(icon: "magnifyingglass", action: onSearchClicked)
                }
            )
            .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass)
        }
        .background(appTheme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .edgesIgnoringSafeArea(.top)
        .onDisappear {
            selectedShow = nil
        }
        .onChange(of: isRefreshing) { _, newValue in
            if !newValue, isRefreshingLocal {
                withAnimation(.easeOut(duration: AnimationConstants.defaultDuration)) {
                    isRefreshingLocal = false
                }
            }
        }
        .toastView(toast: $toast)
        .sheet(item: $selectedEpisode) { episode in
            if let episodeSheetContent {
                episodeSheetContent(episode)
            }
        }
    }

    @ViewBuilder
    private var discoverScrollView: some View {
        if #available(iOS 18.0, *) {
            scrollViewContent
                .onScrollGeometryChange(for: CGFloat.self) { geometry in
                    geometry.contentOffset.y
                } action: { _, newValue in
                    if !isRefreshingLocal, isScrollInteracting {
                        pullOffset = max(0, -newValue)
                    }
                }
                .onScrollPhaseChange { oldPhase, newPhase in
                    isScrollInteracting = newPhase == .interacting
                    if oldPhase == .interacting {
                        if !isRefreshingLocal, pullOffset >= RefreshConstants.threshold {
                            isRefreshingLocal = true
                            onRefresh()
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

                    headerContent(shows: featuredShows)
                        .frame(width: proxy.size.width, height: CarouselConstants.headerHeight + max(scrollY, 0))
                        .offset(y: -max(scrollY, 0))
                        .overlay(alignment: .bottom) {
                            showInfoOverlay(featuredShows)
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

                discoverListContent
            }
        }
        .coordinateSpace(name: "discoverScroll")
    }

    @ViewBuilder
    private func headerContent(shows: [SwiftShow]) -> some View {
        if shows.isEmpty {
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle())
                .scaleEffect(1.5)
                .tint(appTheme.colors.accent)
        } else {
            CarouselView(
                items: shows,
                currentIndex: $currentIndex,
                onItemScrolled: { item in
                    selectedShow = item
                    onCarouselIndexChanged(currentIndex)
                },
                onDraggingChanged: { isDragging in
                    isDraggingCarousel = isDragging
                }
            ) { index in
                carouselItemView(item: shows[index])
            }
        }
    }

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
                onShowClicked(item.traktId)
            }
        }
    }

    private func showInfoOverlay(_ shows: [SwiftShow]) -> some View {
        VStack(alignment: .leading) {
            Text(selectedShow?.title ?? "")
                .textStyle(appTheme.typography.headlineLarge)
                .foregroundColor(appTheme.colors.onSurface)
                .lineLimit(1)
                .frame(maxWidth: .infinity, alignment: .center)

            if let overview = selectedShow?.overview {
                Text(overview)
                    .textStyle(appTheme.typography.bodyLarge)
                    .foregroundColor(appTheme.colors.onSurface.opacity(0.9))
                    .multilineTextAlignment(.leading)
                    .lineLimit(4)
            }

            customIndicator(shows)
                .padding(.top, appTheme.spacing.xSmall)
                .frame(maxWidth: .infinity, alignment: .center)
        }
        .padding(.horizontal)
        .padding(.bottom, appTheme.spacing.medium)
        .frame(maxWidth: .infinity, alignment: .leading)
        .allowsHitTesting(false)
        .background(
            LinearGradient(
                stops: [
                    .init(color: appTheme.colors.background, location: 0),
                    .init(color: appTheme.colors.background, location: 0.3),
                    .init(color: appTheme.colors.background.opacity(0.9), location: 0.5),
                    .init(color: appTheme.colors.background.opacity(0.7), location: 0.65),
                    .init(color: appTheme.colors.background.opacity(0.4), location: 0.8),
                    .init(color: .clear, location: 1.0),
                ],
                startPoint: .bottom,
                endPoint: .top
            )
            .padding(.top, -120)
            .allowsHitTesting(false)
        )
    }

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

    private var discoverListContent: some View {
        VStack {
            NextEpisodesSection(
                title: upNextTitle,
                episodes: nextEpisodes,
                chevronStyle: .chevronOnly,
                onEpisodeClick: onNextEpisodeClicked,
                onEpisodeLongPress: onNextEpisodeLongPress
            )

            HorizontalItemListView(
                title: trendingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: trendingToday,
                onClick: onShowClicked,
                onMoreClicked: onTrendingClicked
            )

            HorizontalItemListView(
                title: upcomingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: upcomingShows,
                onClick: onShowClicked,
                onMoreClicked: onUpcomingClicked
            )

            HorizontalItemListView(
                title: popularTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: popularShows,
                onClick: onShowClicked,
                onMoreClicked: onPopularClicked
            )

            HorizontalItemListView(
                title: topRatedTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: topRatedShows,
                onClick: onShowClicked,
                onMoreClicked: onTopRatedClicked
            )
        }
        .padding(.top, appTheme.spacing.medium)
        .background(appTheme.colors.background)
        .offset(y: -10)
    }

    private var emptyView: some View {
        EmptyStateView(
            systemName: "list.bullet.below.rectangle",
            title: emptyContentText,
            message: missingApiKeyText,
            buttonText: retryText,
            action: onRefresh
        )
    }

    private enum CarouselConstants {
        static let headerHeight: CGFloat = 580
        static let fixedImageHeight: CGFloat = headerHeight
    }

    private enum RefreshConstants {
        static let threshold: CGFloat = 80
        static let indicatorTopPadding: CGFloat = 100
    }
}
