import SwiftUI

public struct SeasonDetailsScreen: View {
    @Theme private var appTheme

    private let seasonName: String
    private let imageUrl: String?
    private let seasonOverview: String
    private let episodeCount: Int64
    private let watchProgress: Float
    private let expandEpisodeItems: Bool
    private let isSeasonWatched: Bool
    private let isRefreshing: Bool
    private let showError: Bool
    private let seasonImages: [ShowPosterImage]
    private let episodes: [SwiftEpisode]
    private let casts: [SwiftCast]
    private let errorTitle: String
    private let errorRetryText: String
    private let overviewTitle: String
    private let episodesTitle: String
    private let seasonImagesCountFormat: (Int) -> String
    private let dayLabelFormat: (Int) -> String
    private let tbdLabel: String
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
        seasonImagesCountFormat: @escaping (Int) -> String,
        dayLabelFormat: @escaping (Int) -> String,
        tbdLabel: String,
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
        self.seasonImagesCountFormat = seasonImagesCountFormat
        self.dayLabelFormat = dayLabelFormat
        self.tbdLabel = tbdLabel
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

    @State private var showGlass: Double = 0
    @State private var progressViewOffset: CGFloat = 0

    public var body: some View {
        ZStack {
            appTheme.colors.background.edgesIgnoringSafeArea(.all)

            if showError {
                EmptyStateView(
                    systemName: "exclamationmark.triangle",
                    title: errorTitle,
                    buttonText: errorRetryText,
                    action: onRetry
                )
            } else {
                seasonDetailsContent
            }
        }
        .ignoresSafeArea()
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .swipeBackGesture(onSwipe: onBack)
        .overlay(
            VStack(spacing: 0) {
                GlassToolbar(
                    title: seasonName,
                    opacity: showGlass,
                    isLoading: isRefreshing,
                    leadingIcon: {
                        GlassButton(icon: "chevron.left", action: onBack)
                            .opacity(1 - showGlass)
                    }
                )
                ProgressView(value: watchProgress, total: 1)
                    .progressViewStyle(RoundedRectProgressViewStyle())
                    .offset(y: progressViewOffset)
            },
            alignment: .top
        )
        .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass)
        .edgesIgnoringSafeArea(.top)
        .sheet(isPresented: $showGallery) {
            ImageGalleryContentView(items: seasonImages)
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
                if !seasonOverview.isEmpty {
                    Text(overviewTitle)
                        .textStyle(appTheme.typography.titleLarge)
                        .foregroundColor(appTheme.colors.onSurface)
                        .lineLimit(1)
                        .padding(.top, appTheme.spacing.large)
                        .padding(.horizontal)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    OverviewBoxView(overview: seasonOverview)
                        .padding()
                }

                EpisodeListView(
                    title: episodesTitle,
                    episodeCount: episodeCount,
                    watchProgress: watchProgress,
                    expandEpisodeItems: expandEpisodeItems,
                    showSeasonWatchStateDialog: false,
                    isSeasonWatched: isSeasonWatched,
                    items: episodes,
                    dayLabelFormat: dayLabelFormat,
                    tbdLabel: tbdLabel,
                    onEpisodeHeaderClicked: onEpisodeHeaderClicked,
                    onWatchedStateClicked: onWatchedStateClicked,
                    onEpisodeWatchToggle: onEpisodeWatchToggle,
                    onEpisodeTapped: onEpisodeTapped
                )

                Spacer().frame(height: appTheme.spacing.large)

                CastListView(casts: casts)
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
                imageUrl: imageUrl,
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
                    HStack(spacing: 16) {
                        Image(systemName: "photo.fill.on.rectangle.fill")
                            .resizable()
                            .frame(width: 28.0, height: 28.0)
                            .fontDesign(.rounded)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                            .alignmentGuide(.view) { d in
                                d[HorizontalAlignment.leading]
                            }

                        Text(seasonImagesCountFormat(seasonImages.count))
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundColor(appTheme.colors.onSurface)
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
