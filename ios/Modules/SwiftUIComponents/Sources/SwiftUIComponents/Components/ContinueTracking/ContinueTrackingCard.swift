import SwiftUI

public struct ContinueTrackingCard: View {
    @Theme private var theme

    private let episode: SwiftContinueTrackingEpisode
    private let dayLabelFormat: (_ count: Int) -> String
    private let onMarkWatched: () -> Void

    public init(
        episode: SwiftContinueTrackingEpisode,
        dayLabelFormat: @escaping (_ count: Int) -> String,
        onMarkWatched: @escaping () -> Void
    ) {
        self.episode = episode
        self.dayLabelFormat = dayLabelFormat
        self.onMarkWatched = onMarkWatched
    }

    public var body: some View {
        HStack(spacing: 0) {
            PosterItemView(
                title: nil,
                posterUrl: episode.imageUrl,
                posterWidth: DimensionConstants.imageWidth,
                posterHeight: DimensionConstants.cardHeight
            )
            .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.cardHeight)
            .clipped()

            HStack {
                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(episode.episodeNumberFormatted)
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                        .lineLimit(1)

                    Text(episode.episodeTitle)
                        .textStyle(theme.typography.bodyMedium)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                        .lineLimit(2)
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                if let daysUntilAir = episode.daysUntilAir, daysUntilAir > 0 {
                    VStack(spacing: 0) {
                        Text("\(daysUntilAir)")
                            .textStyle(theme.typography.titleLarge)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                        Text(dayLabelFormat(Int(daysUntilAir)))
                            .textStyle(theme.typography.labelSmall)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                    }
                    .padding(.trailing, theme.spacing.small)
                } else {
                    ZStack {
                        Circle()
                            .fill(episode.isWatched ? theme.colors.success : theme.colors.grey)
                            .frame(width: DimensionConstants.checkmarkSize, height: DimensionConstants.checkmarkSize)
                        Image(systemName: "checkmark")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.white)
                    }
                    .frame(width: DimensionConstants.tapTargetSize, height: DimensionConstants.tapTargetSize)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        onMarkWatched()
                    }
                }
            }
            .padding(.horizontal, theme.spacing.small)
            .padding(.vertical, theme.spacing.medium)
        }
        .frame(width: DimensionConstants.cardWidth, height: DimensionConstants.cardHeight)
        .background(theme.colors.surfaceVariant)
        .cornerRadius(theme.shapes.medium)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

private enum DimensionConstants {
    static let cardWidth: CGFloat = 300
    static let cardHeight: CGFloat = 120
    static let imageWidth: CGFloat = 100
    static let checkmarkSize: CGFloat = 24
    static let tapTargetSize: CGFloat = 50
}

#Preview {
    VStack(spacing: 16) {
        ContinueTrackingCard(
            episode: SwiftContinueTrackingEpisode(
                episodeId: 123,
                seasonId: 1,
                showId: 1,
                episodeNumber: 3,
                seasonNumber: 2,
                episodeNumberFormatted: "S02 | E03",
                episodeTitle: "Re:start",
                imageUrl: nil,
                isWatched: false,
                daysUntilAir: nil
            ),
            dayLabelFormat: { count in count == 1 ? "day" : "days" },
            onMarkWatched: {}
        )

        ContinueTrackingCard(
            episode: SwiftContinueTrackingEpisode(
                episodeId: 122,
                seasonId: 1,
                showId: 1,
                episodeNumber: 2,
                seasonNumber: 2,
                episodeNumberFormatted: "S02 | E02",
                episodeTitle: "Previous Episode",
                imageUrl: nil,
                isWatched: true,
                daysUntilAir: nil
            ),
            dayLabelFormat: { count in count == 1 ? "day" : "days" },
            onMarkWatched: {}
        )

        ContinueTrackingCard(
            episode: SwiftContinueTrackingEpisode(
                episodeId: 124,
                seasonId: 1,
                showId: 1,
                episodeNumber: 5,
                seasonNumber: 2,
                episodeNumberFormatted: "S02 | E05",
                episodeTitle: "Upcoming Episode",
                imageUrl: nil,
                isWatched: false,
                daysUntilAir: 14
            ),
            dayLabelFormat: { count in count == 1 ? "day" : "days" },
            onMarkWatched: {}
        )
    }
    .padding()
}
