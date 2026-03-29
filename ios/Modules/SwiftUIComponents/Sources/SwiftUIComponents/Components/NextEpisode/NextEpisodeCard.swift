import SwiftUI

public struct NextEpisodeCard: View {
    @Theme private var theme

    private let episode: SwiftNextEpisode
    private let onEpisodeClick: () -> Void

    public init(
        episode: SwiftNextEpisode,
        onEpisodeClick: @escaping () -> Void
    ) {
        self.episode = episode
        self.onEpisodeClick = onEpisodeClick
    }

    public var body: some View {
        ZStack {
            LazyResizableImage(
                url: episode.imageUrl,
                imageType: .backdrop,
                size: CGSize(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)
            )
            .scaledToFill()
            .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)
            .clipped()

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
                            .textStyle(theme.typography.labelSmall)
                            .foregroundColor(theme.colors.onPrimary)
                            .padding(.horizontal, theme.spacing.xxSmall + 2)
                            .padding(.vertical, theme.spacing.xxxSmall)
                            .background(Color.black.opacity(0.6))
                            .cornerRadius(theme.shapes.small)
                            .padding(theme.spacing.xSmall)
                    }
                    Spacer()
                }
            }

            VStack {
                Spacer()
                HStack {
                    VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                        Text(episode.showName)
                            .textStyle(theme.typography.titleMedium)
                            .foregroundColor(theme.colors.onPrimary)
                            .lineLimit(1)

                        Text(episode.episodeNumber)
                            .textStyle(theme.typography.bodyMedium)
                            .foregroundColor(theme.colors.onPrimary.opacity(0.8))
                            .lineLimit(1)
                    }
                    Spacer()
                }
                .padding(theme.spacing.small)
            }
        }
        .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)
        .cornerRadius(theme.shapes.medium)
        .shadow(color: Color.black.opacity(0.2), radius: 4, x: 0, y: 2)
        .onTapGesture {
            onEpisodeClick()
        }
    }
}

private enum DimensionConstants {
    static let imageWidth: CGFloat = 300
    static let imageHeight: CGFloat = 160
}

#Preview {
    VStack {
        NextEpisodeCard(
            episode: SwiftNextEpisode(
                showTraktId: 1,
                showName: "The Walking Dead: Daryl Dixon",
                imageUrl: "/still.jpg",
                episodeId: 123,
                episodeTitle: "L'âme Perdue",
                episodeNumber: "S02E01",
                runtime: "45 min",
                overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                badge: .new
            ),
            onEpisodeClick: {}
        )
        .padding()
    }
}
