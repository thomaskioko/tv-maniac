import DesignSystem
import SwiftUI
import YouTubePlayerKit

public struct YoutubeItemView: View {
    @Environment(\.appTheme) private var theme
    @State private var isLoading = false
    @State private var player: YouTubePlayer?

    private let openInYouTube: Bool
    private let key: String
    private let name: String
    private let thumbnailUrl: String
    private let onError: ((Error) -> Void)?

    private var scaledImageWidth: CGFloat {
        DimensionConstants.imageWidth * ImageDimens.landscapeWidthScale
    }

    private var scaledImageHeight: CGFloat {
        DimensionConstants.imageHeight * ImageDimens.landscapeWidthScale
    }

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
    }

    public var body: some View {
        ZStack {
            if let player {
                YouTubePlayerView(player)
                    .frame(
                        width: scaledImageWidth,
                        height: scaledImageHeight
                    )
            }

            VStack {
                LazyResizableImage(
                    url: thumbnailUrl,
                    size: CGSize(width: scaledImageWidth, height: scaledImageHeight),
                    placeholderIcon: "play.fill"
                )
                .aspectRatio(contentMode: .fill)
                .frame(
                    width: scaledImageWidth,
                    height: scaledImageHeight
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
                .appShadow(theme.shadows.small)

                HStack {
                    Text(name)
                        .textStyle(theme.typography.bodyMedium)
                        .foregroundStyle(.appOnSurfaceVariant)
                        .lineLimit(DimensionConstants.lineLimits)
                        .padding([.trailing], theme.spacing.medium)

                    Spacer()
                }
            }
            .frame(width: scaledImageWidth)
        }
        .frame(width: scaledImageWidth)
        .accessibilityElement(children: .combine)
        .accessibilityLabel(name)
        .onTapGesture(perform: openVideo)
    }

    private var overlay: some View {
        ZStack {
            theme.colors.scrim.opacity(DimensionConstants.overlayOpacity)
            if isLoading {
                ProgressView()
                    .tint(theme.colors.onScrim)
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
                    .foregroundStyle(.appOnScrim, .appScrim.opacity(0.5))
                    .scaledToFit()
                    .imageScale(.medium)
                    .padding(theme.spacing.medium)
            }
        }
        .frame(
            width: scaledImageWidth,
            height: scaledImageHeight
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
