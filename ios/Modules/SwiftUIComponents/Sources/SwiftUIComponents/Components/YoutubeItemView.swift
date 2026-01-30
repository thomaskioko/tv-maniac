import SwiftUI
import YouTubePlayerKit

public struct YoutubeItemView: View {
    @Theme private var theme
    @State private var isLoading = false

    private let openInYouTube: Bool
    private let key: String
    private let name: String
    private let thumbnailUrl: String
    private let player: YouTubePlayer
    private let onError: ((Error) -> Void)?

    public init(
        openInYouTube: Bool,
        key: String,
        name: String,
        thumbnailUrl: String,
        onError: ((Error) -> Void)? = nil
    ) {
        self.openInYouTube = openInYouTube
        self.key = key
        self.name = name
        self.thumbnailUrl = thumbnailUrl
        self.onError = onError

        player = YouTubePlayer(
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
    }

    public var body: some View {
        ZStack {
            YouTubePlayerView(player)
                .frame(
                    width: DimensionConstants.imageWidth,
                    height: DimensionConstants.imageHeight
                )
                .opacity(0)

            VStack {
                LazyResizableImage(
                    url: thumbnailUrl,
                    size: CGSize(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)
                ) { state in
                    if let image = state.image {
                        image.resizable()
                    } else {
                        placeholder
                    }
                }
                .aspectRatio(contentMode: .fill)
                .frame(
                    width: DimensionConstants.imageWidth,
                    height: DimensionConstants.imageHeight
                )
                .clipShape(
                    RoundedRectangle(
                        cornerRadius: theme.shapes.medium,
                        style: .continuous
                    )
                )
                .overlay {
                    overlay
                }
                .shadow(radius: 2.5)

                HStack {
                    Text(name)
                        .textStyle(theme.typography.bodyMedium)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                        .lineLimit(DimensionConstants.lineLimits)
                        .padding([.trailing], theme.spacing.medium)

                    Spacer()
                }
            }
            .frame(width: DimensionConstants.imageWidth)
        }
        .frame(width: DimensionConstants.imageWidth)
        .accessibilityElement(children: .combine)
        .accessibilityLabel(name)
        .onTapGesture(perform: openVideo)
    }

    private var placeholder: some View {
        ZStack {
            theme.colors.surfaceVariant
            Image(systemName: "play.fill")
                .foregroundColor(theme.colors.onPrimary)
                .imageScale(.medium)
        }
        .transition(.opacity)
        .frame(
            width: DimensionConstants.imageWidth,
            height: DimensionConstants.imageHeight
        )
        .clipShape(
            RoundedRectangle(
                cornerRadius: theme.shapes.medium,
                style: .continuous
            )
        )
    }

    private var overlay: some View {
        ZStack {
            Color.black.opacity(DimensionConstants.overlayOpacity)
            if isLoading {
                ProgressView()
                    .tint(theme.colors.onPrimary)
                    .frame(
                        width: DimensionConstants.overlayWidth,
                        height: DimensionConstants.overlayHeight,
                        alignment: .center
                    )
                    .padding(theme.spacing.medium)
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 4) {
                            withAnimation {
                                isLoading = false
                            }
                        }
                    }
            } else {
                Image(systemName: "play.circle.fill")
                    .resizable()
                    .frame(
                        width: DimensionConstants.overlayWidth,
                        height: DimensionConstants.overlayHeight,
                        alignment: .center
                    )
                    .symbolRenderingMode(.palette)
                    .foregroundStyle(theme.colors.onPrimary, theme.colors.surfaceVariant.opacity(0.6))
                    .scaledToFit()
                    .imageScale(.medium)
                    .padding(theme.spacing.medium)
            }
        }
        .frame(
            width: DimensionConstants.imageWidth,
            height: DimensionConstants.imageHeight
        )
        .clipShape(
            RoundedRectangle(
                cornerRadius: theme.shapes.medium,
                style: .continuous
            )
        )
    }

    private func openVideo() {
        if openInYouTube {
            if let url = urlBuilder(path: key) {
                UIApplication.shared.open(url)
            }
        } else {
            isLoading = true
            Task { @MainActor in
                do {
                    try await player.play()
                } catch {
                    print("Failed to play video: \(error)")
                    isLoading = false
                    onError?(error)
                }
            }
        }
    }
}

private func urlBuilder(path: String? = nil) -> URL? {
    guard let path else {
        return nil
    }
    var components = URLComponents()
    components.scheme = "https"
    components.host = "www.youtube.com"
    components.path = "/embed/\(path)"
    return components.url
}

private enum DimensionConstants {
    static let imageRadius: CGFloat = 8
    static let imageShadow: CGFloat = 2.5
    static let imageWidth: CGFloat = 260
    static let imageHeight: CGFloat = 140
    static let overlayOpacity: Double = 0.2
    static let overlayWidth: CGFloat = 50
    static let overlayHeight: CGFloat = 50
    static let lineLimits: Int = 1
}

#Preview {
    YoutubeItemView(
        openInYouTube: false,
        key: "XZ8daibM3AE",
        name: "Series Trailer",
        thumbnailUrl: "https://i.ytimg.com/vi/XZ8daibM3AE/hqdefault.jpg"
    )
}
