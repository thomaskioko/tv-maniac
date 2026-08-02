import Components
import DesignSystem
import Models
import SwiftUI

public struct MyShowsDetailedCardView: View {
    @Environment(\.appTheme) private var theme

    private let episode: SwiftNextEpisode
    private let onItemClicked: () -> Void

    public init(
        episode: SwiftNextEpisode,
        onItemClicked: @escaping () -> Void
    ) {
        self.episode = episode
        self.onItemClicked = onItemClicked
    }

    private var progress: Float {
        guard episode.totalCount > 0 else { return 0 }
        return Float(episode.watchedCount) / Float(episode.totalCount)
    }

    public var body: some View {
        Button(action: onItemClicked) {
            ZStack(alignment: .bottomLeading) {
                Color.clear
                    .aspectRatio(16.0 / 9.0, contentMode: .fit)
                    .overlay { LazyResizableImage(url: episode.imageUrl) }
                    .clipped()

                LinearGradient(
                    colors: [
                        .clear,
                        theme.colors.surface.opacity(0.4),
                        theme.colors.surface.opacity(0.7),
                        theme.colors.surface.opacity(0.9),
                        theme.colors.surface,
                    ],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 96)
                .frame(maxWidth: .infinity, alignment: .bottom)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(episode.showName)
                        .textStyle(theme.typography.titleMedium)
                        .foregroundStyle(.appOnSurface)
                        .lineLimit(1)

                    metadataText([episode.episodeNumber, episode.episodeTitle], accent: theme.colors.accent)
                        .textStyle(theme.typography.bodySmall)
                        .foregroundStyle(.appOnSurfaceVariant)
                        .lineLimit(1)

                    ProgressView(value: Double(progress), total: 1)
                        .progressViewStyle(RoundedRectProgressViewStyle())
                }
                .padding(theme.spacing.medium)
            }
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium, style: .continuous))
            .appShadow(theme.shadows.medium)
            .padding(.horizontal, theme.spacing.small)
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    ScrollView {
        VStack(spacing: TvManiacSpacingScheme.default.medium) {
            MyShowsDetailedCardView(
                episode: SwiftNextEpisode(
                    showId: 1,
                    showName: "The Walking Dead: Daryl Dixon",
                    imageUrl: nil,
                    episodeId: 123,
                    episodeTitle: "L'ame Perdue",
                    episodeNumber: "S02 | E01",
                    runtime: "45 min",
                    overview: "Daryl washes ashore in France.",
                    watchedCount: 3,
                    totalCount: 8
                ),
                onItemClicked: {}
            )

            MyShowsDetailedCardView(
                episode: SwiftNextEpisode(
                    showId: 2,
                    showName: "Severance",
                    imageUrl: nil,
                    episodeId: 456,
                    episodeTitle: "Woe's Hollow",
                    episodeNumber: "S02 | E05",
                    runtime: "52 min",
                    overview: "The severed team goes on a retreat.",
                    watchedCount: 5,
                    totalCount: 10
                ),
                onItemClicked: {}
            )
        }
        .padding()
    }
}
