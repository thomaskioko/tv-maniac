import Components
import DesignSystem
import Models
import SwiftUI

public struct TrailerListView: View {
    @Environment(\.appTheme) private var theme

    private let title: String
    private let trailers: [SwiftTrailer]
    private let openInYouTube: Bool
    private let onMoreClicked: () -> Void
    private let onError: ((Error) -> Void)?

    public init(
        title: String,
        trailers: [SwiftTrailer],
        openInYouTube: Bool,
        onMoreClicked: @escaping () -> Void,
        onError: ((Error) -> Void)? = nil
    ) {
        self.title = title
        self.trailers = trailers
        self.openInYouTube = openInYouTube
        self.onMoreClicked = onMoreClicked
        self.onError = onError
    }

    public var body: some View {
        if !trailers.isEmpty {
            VStack {
                ChevronTitle(
                    title: title,
                    chevronStyle: .chevronOnly,
                    action: onMoreClicked
                )

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack {
                        ForEach(trailers, id: \.key) { trailer in
                            YoutubeItemView(
                                openInYouTube: openInYouTube,
                                key: trailer.key,
                                name: trailer.name,
                                thumbnailUrl: trailer.youtubeThumbnailUrl,
                                onError: onError
                            )
                            .padding(.horizontal, theme.spacing.xxSmall)
                            .padding(.leading, trailer.key == trailers.first?.key ? theme.spacing.medium : 0)
                            .padding(.trailing, trailer.key == trailers.last?.key ? theme.spacing.medium : 0)
                        }
                    }
                }
            }
        }
    }
}

#Preview {
    VStack {
        TrailerListView(
            title: "Trailers",
            trailers: [
                .init(
                    showId: 123,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: "https://i.ytimg.com/vi/XZ8daibM3AE/hqdefault.jpg"
                ),
                .init(
                    showId: 1234,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: "https://i.ytimg.com/vi/XZ8daibM3AE/hqdefault.jpg"
                ),
            ],
            openInYouTube: false,
            onMoreClicked: {}
        )
    }
}
