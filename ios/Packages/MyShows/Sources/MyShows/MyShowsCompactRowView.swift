import Components
import DesignSystem
import Models
import SwiftUI

public struct MyShowsCompactRowView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    private let episode: SwiftNextEpisode
    private let onItemClicked: () -> Void

    public init(
        episode: SwiftNextEpisode,
        onItemClicked: @escaping () -> Void
    ) {
        self.episode = episode
        self.onItemClicked = onItemClicked
    }

    public var body: some View {
        Button(action: onItemClicked) {
            HStack(spacing: 0) {
                // The artwork sits flush against the card edge and sets the row height.
                PosterItemView(
                    title: nil,
                    posterUrl: episode.imageUrl,
                    posterWidth: ImageDimens.compactThumbnailWidth(widthSizeClass),
                    aspectRatio: ImageType.backdrop.aspect,
                    posterRadius: 0
                )

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(episode.showName)
                        .textStyle(theme.typography.titleSmall)
                        .foregroundStyle(.appOnSurface)
                        .lineLimit(1)

                    metadataText([episode.episodeNumber, episode.episodeTitle], accent: theme.colors.accent)
                        .textStyle(theme.typography.bodySmall)
                        .foregroundStyle(.appOnSurfaceVariant)
                        .lineLimit(1)
                }
                .padding(theme.spacing.small)

                Spacer(minLength: 0)
            }
            .background(.appSurface)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium, style: .continuous))
            .appShadow(theme.shadows.medium)
            .padding(.horizontal, theme.spacing.small)
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    VStack(spacing: TvManiacSpacingScheme.default.xxSmall) {
        MyShowsCompactRowView(
            episode: SwiftNextEpisode(
                showId: 1,
                showName: "The Walking Dead: Daryl Dixon",
                imageUrl: nil,
                episodeId: 123,
                episodeTitle: "L'ame Perdue",
                episodeNumber: "S02 | E01",
                runtime: "45 min",
                overview: "Daryl washes ashore in France."
            ),
            onItemClicked: {}
        )

        MyShowsCompactRowView(
            episode: SwiftNextEpisode(
                showId: 2,
                showName: "Severance",
                imageUrl: nil,
                episodeId: 456,
                episodeTitle: "Woe's Hollow",
                episodeNumber: "S02 | E05",
                runtime: "52 min",
                overview: "The severed team goes on a retreat."
            ),
            onItemClicked: {}
        )
    }
    .padding()
}
