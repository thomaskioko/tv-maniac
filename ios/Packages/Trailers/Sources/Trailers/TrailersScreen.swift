import Components
import DesignSystem
import Models
import SwiftUI
import YouTubePlayerKit

public enum TrailersScreenState: Equatable {
    case loading
    case content(selected: SwiftTrailer?, trailers: [SwiftTrailer], moreTrailersTitle: String)
    case error(message: String, retryLabel: String)
}

public struct TrailersScreen: View {
    public struct State: Equatable {
        public let title: String
        public let screenState: TrailersScreenState

        public init(
            title: String,
            screenState: TrailersScreenState
        ) {
            self.title = title
            self.screenState = screenState
        }
    }

    @Environment(\.appTheme) private var theme

    private let state: State
    private let onTrailerSelected: (String) -> Void
    private let onVideoError: (String) -> Void
    private let onRetry: () -> Void
    private let onBack: () -> Void

    public init(
        state: State,
        onTrailerSelected: @escaping (String) -> Void,
        onVideoError: @escaping (String) -> Void,
        onRetry: @escaping () -> Void,
        onBack: @escaping () -> Void
    ) {
        self.state = state
        self.onTrailerSelected = onTrailerSelected
        self.onVideoError = onVideoError
        self.onRetry = onRetry
        self.onBack = onBack
    }

    public var body: some View {
        content
            .contentMargins(.top, toolbarInset + theme.spacing.medium)
            .appScreen()
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarBackButtonHidden(true)
            .navigationBarColor(backgroundColor: .clear)
            .swipeBackGesture(onSwipe: onBack)
            .overlay(
                GlassToolbar(
                    title: state.title,
                    opacity: 1.0,
                    leadingIcon: {
                        GlassButton(icon: "chevron.left", action: onBack)
                    }
                ),
                alignment: .top
            )
            .edgesIgnoringSafeArea(.top)
    }

    @ViewBuilder
    private var content: some View {
        switch state.screenState {
        case .loading:
            CenteredFullScreenView {
                LoadingIndicatorView()
            }
        case let .content(selected, trailers, moreTrailersTitle):
            trailersContent(selected: selected, trailers: trailers, moreTrailersTitle: moreTrailersTitle)
        case let .error(message, retryLabel):
            EmptyStateView(
                systemName: "exclamationmark.circle",
                title: message,
                buttonText: retryLabel,
                action: onRetry
            )
        }
    }

    private func trailersContent(
        selected: SwiftTrailer?,
        trailers: [SwiftTrailer],
        moreTrailersTitle: String
    ) -> some View {
        ScrollView(.vertical, showsIndicators: false) {
            LazyVStack(alignment: .leading, spacing: theme.spacing.small) {
                if let selected {
                    TrailerPlayerView(
                        key: selected.key,
                        thumbnailUrl: selected.youtubeThumbnailUrl,
                        onError: onVideoError
                    )
                    .id("player-\(selected.key)")
                }

                ChevronTitle(title: moreTrailersTitle)

                ForEach(trailers, id: \.key) { trailer in
                    trailerRow(trailer)
                }
            }
        }
    }

    private func trailerRow(_ trailer: SwiftTrailer) -> some View {
        HStack(alignment: .top, spacing: theme.spacing.xSmall) {
            LazyResizableImage(
                url: trailer.youtubeThumbnailUrl,
                size: CGSize(width: DimensionConstants.rowImageWidth, height: DimensionConstants.rowImageHeight),
                placeholderIcon: "play.fill"
            )
            .frame(width: DimensionConstants.rowImageWidth, height: DimensionConstants.rowImageHeight)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium, style: .continuous))

            Text(trailer.name)
                .textStyle(theme.typography.labelLarge)
                .foregroundStyle(.appOnSurface)
                .lineLimit(2)
                .multilineTextAlignment(.leading)

            Spacer(minLength: 0)
        }
        .padding(.horizontal, theme.spacing.xSmall)
        .contentShape(Rectangle())
        .accessibilityElement(children: .combine)
        .accessibilityLabel(trailer.name)
        .onTapGesture { onTrailerSelected(trailer.key) }
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }
}

private struct TrailerPlayerView: View {
    @Environment(\.appTheme) private var theme
    @State private var player: YouTubePlayer?

    let key: String
    let thumbnailUrl: String
    let onError: (String) -> Void

    var body: some View {
        ZStack {
            if let player {
                YouTubePlayerView(player)
            } else {
                LazyResizableImage(
                    url: thumbnailUrl,
                    aspectRatio: 16 / 9,
                    placeholderIcon: "play.fill"
                )
                .overlay {
                    playOverlay
                }
                .contentShape(Rectangle())
                .onTapGesture(perform: playVideo)
            }
        }
        .frame(maxWidth: .infinity)
        .aspectRatio(16 / 9, contentMode: .fit)
        .accessibilityElement(children: .combine)
    }

    private var playOverlay: some View {
        ZStack {
            theme.colors.scrim.opacity(DimensionConstants.overlayOpacity)

            Image(systemName: "play.circle.fill")
                .resizable()
                .frame(
                    width: DimensionConstants.overlayIconSize,
                    height: DimensionConstants.overlayIconSize
                )
                .symbolRenderingMode(.palette)
                .foregroundStyle(.appOnScrim, .appScrim.opacity(0.5))
        }
    }

    private func playVideo() {
        let newPlayer = YouTubePlayer(
            source: .video(id: key),
            parameters: .init(
                autoPlay: false,
                loopEnabled: true,
                showControls: true
            ),
            configuration: .init(
                fullscreenMode: .system,
                allowsPictureInPictureMediaPlayback: false,
                automaticallyAdjustsContentInsets: true
            )
        )
        player = newPlayer
        Task { @MainActor in
            do {
                try await newPlayer.play()
            } catch {
                player = nil
                onError(error.localizedDescription)
            }
        }
    }
}

private enum DimensionConstants {
    static let rowImageWidth: CGFloat = 140
    static let rowImageHeight: CGFloat = 80
    static let overlayOpacity: Double = 0.2
    static let overlayIconSize: CGFloat = 50
}

#Preview("Trailers") {
    TrailersScreen(
        state: TrailersScreen.State(
            title: "Trailers",
            screenState: .content(
                selected: .init(
                    showId: 1,
                    key: "XZ8daibM3AE",
                    name: "Official Trailer",
                    youtubeThumbnailUrl: ""
                ),
                trailers: [
                    .init(showId: 1, key: "XZ8daibM3AE", name: "Official Trailer", youtubeThumbnailUrl: ""),
                    .init(showId: 1, key: "aB9dEf3", name: "Season 2 Teaser", youtubeThumbnailUrl: ""),
                ],
                moreTrailersTitle: "More Trailers"
            )
        ),
        onTrailerSelected: { _ in },
        onVideoError: { _ in },
        onRetry: {},
        onBack: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Trailers - Loading") {
    TrailersScreen(
        state: TrailersScreen.State(
            title: "Trailers",
            screenState: .loading
        ),
        onTrailerSelected: { _ in },
        onVideoError: { _ in },
        onRetry: {},
        onBack: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Trailers - Error") {
    TrailersScreen(
        state: TrailersScreen.State(
            title: "Trailers",
            screenState: .error(message: "Something went wrong", retryLabel: "Retry")
        ),
        onTrailerSelected: { _ in },
        onVideoError: { _ in },
        onRetry: {},
        onBack: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}
