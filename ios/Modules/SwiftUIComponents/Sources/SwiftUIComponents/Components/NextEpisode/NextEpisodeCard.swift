import SDWebImageSwiftUI
import SwiftUI

public struct NextEpisodeCard: View {
    private let episode: SwiftNextEpisode
    private let onEpisodeClick: (Int64, Int64) -> Void

    public init(
        episode: SwiftNextEpisode,
        onEpisodeClick: @escaping (Int64, Int64) -> Void
    ) {
        self.episode = episode
        self.onEpisodeClick = onEpisodeClick
    }

    public var body: some View {
        Button(action: {
            onEpisodeClick(episode.showId, episode.episodeId)
        }) {
            ZStack {
                if let imageUrl = episode.stillImage ?? episode.showPoster {
                    WebImage(
                        url: URL(string: imageUrl.transformedImageURL),
                        options: [
                            .retryFailed,
                            .highPriority,
                            .scaleDownLargeImages,
                        ],
                        context: [
                            .imageThumbnailPixelSize: CGSize(width: 600, height: 400),
                            .imageForceDecodePolicy: SDImageForceDecodePolicy.never.rawValue,
                        ]
                    ) { image in
                        image
                            .resizable()
                            .scaledToFill()
                    } placeholder: {
                        Rectangle()
                            .fill(Color.gray.opacity(0.3))
                            .overlay(
                                Image(systemName: "tv")
                                    .font(.title)
                                    .foregroundColor(.gray)
                            )
                    }
                    .indicator(.activity)
                    .transition(.opacity)
                    .scaledToFill()
                    .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)
                    .clipped()
                } else {
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)
                        .overlay(
                            Image(systemName: "tv")
                                .font(.title)
                                .foregroundColor(.gray)
                        )
                }

                LinearGradient(
                    gradient: Gradient(colors: [
                        Color.clear,
                        Color.black.opacity(0.7),
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)

                if let runtime = episode.runtime {
                    VStack {
                        HStack {
                            Spacer()
                            Text(runtime)
                                .font(.caption2)
                                .fontWeight(.medium)
                                .foregroundColor(.white)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color.black.opacity(0.6))
                                .cornerRadius(4)
                                .padding(8)
                        }
                        Spacer()
                    }
                }

                VStack {
                    Spacer()
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(episode.showName)
                                .font(.system(size: 16, weight: .medium))
                                .foregroundColor(.white)
                                .lineLimit(1)

                            Text(episode.episodeNumber)
                                .font(.system(size: 14, weight: .regular))
                                .foregroundColor(.white.opacity(0.8))
                                .lineLimit(1)
                        }
                        Spacer()
                    }
                    .padding(12)
                }
            }
        }
        .buttonStyle(PlainButtonStyle())
        .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)
        .cornerRadius(8)
        .shadow(color: .black.opacity(0.2), radius: 4, x: 0, y: 2)
    }
}

private enum DimensionConstants {
    static let imageWidth: CGFloat = 300
    static let imageHeight: CGFloat = 180
}

#Preview {
    VStack {
        NextEpisodeCard(
            episode: SwiftNextEpisode(
                showId: 1,
                showName: "The Walking Dead: Daryl Dixon",
                showPoster: "/poster.jpg",
                episodeId: 123,
                episodeTitle: "L'âme Perdue",
                episodeNumber: "S02E01",
                runtime: "45 min",
                stillImage: "/still.jpg",
                overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                isNew: true
            ),
            onEpisodeClick: { _, _ in }
        )
        .padding()
    }
}
