import Components
import DesignSystem
import Models
import SwiftUI

public struct SeasonDetailsScreen: View {
    public struct State {
        public let seasonName: String
        public let imageUrl: String?
        public let seasonOverview: String
        public let episodeCount: Int64
        public let watchProgress: Float
        public let expandEpisodeItems: Bool
        public let isSeasonWatched: Bool
        public let isRefreshing: Bool
        public let showError: Bool
        public let seasonImages: [ShowPosterImage]
        public let episodes: [SwiftEpisode]
        public let casts: [SwiftCast]
        public let errorTitle: String
        public let errorRetryText: String
        public let overviewTitle: String
        public let episodesTitle: String
        public let tbdLabel: String

        public init(
            seasonName: String,
            imageUrl: String?,
            seasonOverview: String,
            episodeCount: Int64,
            watchProgress: Float,
            expandEpisodeItems: Bool,
            isSeasonWatched: Bool,
            isRefreshing: Bool,
            showError: Bool,
            seasonImages: [ShowPosterImage],
            episodes: [SwiftEpisode],
            casts: [SwiftCast],
            errorTitle: String,
            errorRetryText: String,
            overviewTitle: String,
            episodesTitle: String,
            tbdLabel: String
        ) {
            self.seasonName = seasonName
            self.imageUrl = imageUrl
            self.seasonOverview = seasonOverview
            self.episodeCount = episodeCount
            self.watchProgress = watchProgress
            self.expandEpisodeItems = expandEpisodeItems
            self.isSeasonWatched = isSeasonWatched
            self.isRefreshing = isRefreshing
            self.showError = showError
            self.seasonImages = seasonImages
            self.episodes = episodes
            self.casts = casts
            self.errorTitle = errorTitle
            self.errorRetryText = errorRetryText
            self.overviewTitle = overviewTitle
            self.episodesTitle = episodesTitle
            self.tbdLabel = tbdLabel
        }
    }

    @Environment(\.appTheme) private var appTheme

    private let state: State
    private let seasonImagesCountFormat: (Int) -> String
    private let dayLabelFormat: (Int) -> String
    @Binding private var toast: Toast?
    @Binding private var showGallery: Bool
    private let onBack: () -> Void
    private let onRetry: () -> Void
    private let onGalleryTap: () -> Void
    private let onEpisodeHeaderClicked: () -> Void
    private let onWatchedStateClicked: () -> Void
    private let onEpisodeWatchToggle: (SwiftEpisode) -> Void
    private let onEpisodeTapped: (SwiftEpisode) -> Void

    public init(
        state: State,
        seasonImagesCountFormat: @escaping (Int) -> String,
        dayLabelFormat: @escaping (Int) -> String,
        toast: Binding<Toast?>,
        showGallery: Binding<Bool>,
        onBack: @escaping () -> Void,
        onRetry: @escaping () -> Void,
        onGalleryTap: @escaping () -> Void,
        onEpisodeHeaderClicked: @escaping () -> Void,
        onWatchedStateClicked: @escaping () -> Void,
        onEpisodeWatchToggle: @escaping (SwiftEpisode) -> Void,
        onEpisodeTapped: @escaping (SwiftEpisode) -> Void = { _ in }
    ) {
        self.state = state
        self.seasonImagesCountFormat = seasonImagesCountFormat
        self.dayLabelFormat = dayLabelFormat
        _toast = toast
        _showGallery = showGallery
        self.onBack = onBack
        self.onRetry = onRetry
        self.onGalleryTap = onGalleryTap
        self.onEpisodeHeaderClicked = onEpisodeHeaderClicked
        self.onWatchedStateClicked = onWatchedStateClicked
        self.onEpisodeWatchToggle = onEpisodeWatchToggle
        self.onEpisodeTapped = onEpisodeTapped
    }

    @SwiftUI.State private var showGlass: Double = 0
    @SwiftUI.State private var progressViewOffset: CGFloat = 0

    public var body: some View {
        ZStack {
            if state.showError {
                EmptyStateView(
                    systemName: "exclamationmark.triangle",
                    title: state.errorTitle,
                    buttonText: state.errorRetryText,
                    action: onRetry
                )
            } else {
                seasonDetailsContent
            }
        }
        .appScreen()
        .ignoresSafeArea()
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .swipeBackGesture(onSwipe: onBack)
        .overlay(
            VStack(spacing: 0) {
                GlassToolbar(
                    title: state.seasonName,
                    opacity: showGlass,
                    isLoading: state.isRefreshing,
                    leadingIcon: {
                        GlassButton(icon: "chevron.left", action: onBack)
                            .opacity(1 - showGlass)
                    }
                )
                ProgressView(value: state.watchProgress, total: 1)
                    .progressViewStyle(RoundedRectProgressViewStyle())
                    .offset(y: progressViewOffset)
            },
            alignment: .top
        )
        .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass)
        .edgesIgnoringSafeArea(.top)
        .sheet(isPresented: $showGallery) {
            ImageGalleryContentView(items: state.seasonImages)
        }
        .toastView(toast: $toast)
    }

    private var seasonDetailsContent: some View {
        ParallaxView(
            imageHeight: DimensionConstants.imageHeight,
            collapsedImageHeight: DimensionConstants.collapsedImageHeight,
            header: { proxy in
                headerContent(
                    progress: proxy.getTitleOpacity(
                        geometry: proxy,
                        imageHeight: DimensionConstants.imageHeight,
                        collapsedImageHeight: DimensionConstants.collapsedImageHeight
                    ),
                    headerHeight: proxy.getHeightForHeaderImage(proxy)
                )
            },
            content: {
                if !state.seasonOverview.isEmpty {
                    Text(state.overviewTitle)
                        .textStyle(appTheme.typography.titleLarge)
                        .foregroundStyle(.appOnSurface)
                        .lineLimit(1)
                        .padding(.top, appTheme.spacing.large)
                        .padding(.horizontal)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    OverviewBoxView(overview: state.seasonOverview)
                        .padding()
                }

                EpisodeListView(
                    title: state.episodesTitle,
                    episodeCount: state.episodeCount,
                    watchProgress: state.watchProgress,
                    expandEpisodeItems: state.expandEpisodeItems,
                    showSeasonWatchStateDialog: false,
                    isSeasonWatched: state.isSeasonWatched,
                    items: state.episodes,
                    dayLabelFormat: dayLabelFormat,
                    tbdLabel: state.tbdLabel,
                    onEpisodeHeaderClicked: onEpisodeHeaderClicked,
                    onWatchedStateClicked: onWatchedStateClicked,
                    onEpisodeWatchToggle: onEpisodeWatchToggle,
                    onEpisodeTapped: onEpisodeTapped
                )

                Spacer().frame(height: appTheme.spacing.large)

                CastListView(casts: state.casts)
            },
            onScroll: { offset in
                showGlass = ParallaxConstants.glassOpacity(from: offset)

                let startOffset = CGFloat(245)
                let endOffset = 0
                progressViewOffset = max(CGFloat(endOffset), startOffset + offset)
            }
        )
    }

    private func headerContent(progress: CGFloat, headerHeight: CGFloat) -> some View {
        ZStack(alignment: .bottom) {
            HeaderCoverArtWorkView(
                imageUrl: state.imageUrl,
                posterHeight: headerHeight
            )
            .foregroundStyle(.ultraThinMaterial)
            .overlay(
                LinearGradient(
                    gradient: Gradient(colors: [
                        .clear,
                        .clear,
                        .clear,
                        .clear,
                        .clear,
                        appTheme.colors.background.opacity(0.8),
                        appTheme.colors.background,
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
            .frame(height: headerHeight)

            ZStack(alignment: .bottom) {
                VStack {
                    Spacer()
                    HStack(spacing: appTheme.spacing.medium) {
                        Image(systemName: "photo.fill.on.rectangle.fill")
                            .resizable()
                            .frame(width: 28.0, height: 28.0)
                            .fontDesign(.rounded)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundStyle(.appOnSurfaceVariant)
                            .alignmentGuide(.view) { d in
                                d[HorizontalAlignment.leading]
                            }

                        Text(seasonImagesCountFormat(state.seasonImages.count))
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundStyle(.appOnSurface)
                            .lineLimit(1)
                            .alignmentGuide(.view) { d in
                                d[HorizontalAlignment.center]
                            }

                        Spacer()
                    }
                    .padding(.horizontal, appTheme.spacing.medium)
                    .padding(.vertical, appTheme.spacing.xLarge)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        onGalleryTap()
                    }
                }
                .frame(height: headerHeight)
            }
            .opacity(1 - progress)
        }
        .frame(height: headerHeight)
        .clipped()
    }
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 350
    static let collapsedImageHeight: CGFloat = 120.0
}
