import SwiftUI
import SwiftUIComponents

public struct TrailerListView: View {
    private let trailers: [SwiftTrailer]
    private let openInYouTube: Bool

    public init(trailers: [SwiftTrailer], openInYouTube: Bool) {
        self.trailers = trailers
        self.openInYouTube = openInYouTube
    }

    public var body: some View {
        if !trailers.isEmpty {
            VStack {
                ChevronTitle(title: "Trailers")

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack {
                        ForEach(trailers, id: \.key) { trailer in
                            TrailerItemView(
                                openInYouTube: openInYouTube,
                                key: trailer.key,
                                name: trailer.name,
                                thumbnailUrl: trailer.youtubeThumbnailUrl
                            )
                            .padding(.horizontal, 4)
                            .padding(.leading, trailer.key == self.trailers.first?.key ? 16 : 0)
                            .padding(.trailing, trailer.key == self.trailers.last?.key ? 16 : 0)
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
            openInYouTube: false
        )
    }
}
