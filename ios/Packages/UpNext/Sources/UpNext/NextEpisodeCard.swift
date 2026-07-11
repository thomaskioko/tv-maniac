import Components
import DesignSystem
import Models
import SwiftUI

public struct NextEpisodeCard: View {
    @Environment(\.appTheme) private var theme

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
            .blurEffect()
            .clipped()

            LinearGradient(
                colors: [.clear, theme.colors.scrim.opacity(0.7)],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: DimensionConstants.scrimHeight)
            .frame(
                width: DimensionConstants.imageWidth,
                height: DimensionConstants.imageHeight,
                alignment: .bottom
            )

            if let runtime = episode.runtime {
                VStack {
                    HStack {
                        Spacer()
                        Text(runtime)
                            .textStyle(theme.typography.labelSmall)
                            .foregroundStyle(.appOnScrim)
                            .padding(.horizontal, theme.spacing.xxSmall + 2)
                            .padding(.vertical, theme.spacing.xxxSmall)
                            .background(theme.colors.scrim.opacity(0.6))
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
                            .foregroundStyle(.appOnScrim)
                            .lineLimit(1)

                        Text(episode.episodeNumber)
                            .textStyle(theme.typography.labelLarge)
                            .foregroundStyle(.appOnScrim.opacity(0.8))
                            .lineLimit(1)
                    }
                    Spacer()
                }
                .padding(theme.spacing.small)
            }
        }
        .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)
        .cornerRadius(theme.shapes.small)
        .appShadow(theme.shadows.medium)
        .onTapGesture {
            onEpisodeClick()
        }
    }
}

private enum DimensionConstants {
    static let imageWidth: CGFloat = 300
    static let imageHeight: CGFloat = 160
    static let scrimHeight: CGFloat = 80
}

#Preview {
    VStack {
        NextEpisodeCard(
            episode: SwiftNextEpisode(
                showId: 1,
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
