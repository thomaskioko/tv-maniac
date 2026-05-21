import DesignSystem
import SwiftUI

public struct ContinueTrackingCard: View {
    @Environment(\.appTheme) private var theme

    private let episode: SwiftContinueTrackingEpisode
    private let dayLabelFormat: (_ count: Int) -> String
    private let tbdLabel: String
    private let onMarkWatched: () -> Void
    private let isUpdating: Bool

    public init(
        episode: SwiftContinueTrackingEpisode,
        dayLabelFormat: @escaping (_ count: Int) -> String,
        tbdLabel: String,
        onMarkWatched: @escaping () -> Void,
        isUpdating: Bool = false
    ) {
        self.episode = episode
        self.dayLabelFormat = dayLabelFormat
        self.tbdLabel = tbdLabel
        self.onMarkWatched = onMarkWatched
        self.isUpdating = isUpdating
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
                        .foregroundStyle(.appOnSurface)
                        .lineLimit(1)

                    Text(episode.episodeTitle)
                        .textStyle(theme.typography.bodyMedium)
                        .foregroundStyle(.appOnSurfaceVariant)
                        .lineLimit(2)
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                if episode.hasAired {
                    Button(action: onMarkWatched) {
                        ZStack {
                            Circle()
                                .fill(episode.isWatched ? .appSuccess : .appGrey)
                                .frame(width: DimensionConstants.checkmarkSize, height: DimensionConstants.checkmarkSize)
                            if isUpdating {
                                ProgressView()
                                    .progressViewStyle(.circular)
                                    .tint(.white)
                            } else {
                                Image(systemName: "checkmark")
                                    .font(theme.typography.titleSmall)
                                    .foregroundStyle(.white)
                            }
                        }
                        .frame(width: DimensionConstants.tapTargetSize, height: DimensionConstants.cardHeight)
                        .contentShape(Rectangle())
                    }
                    .buttonStyle(.plain)
                    .disabled(isUpdating)
                } else if let daysUntilAir = episode.daysUntilAir, daysUntilAir > 0 {
                    VStack(spacing: 0) {
                        Text("\(daysUntilAir)")
                            .textStyle(theme.typography.titleLarge)
                            .foregroundStyle(.appOnSurfaceVariant)
                        Text(dayLabelFormat(Int(daysUntilAir)))
                            .textStyle(theme.typography.labelSmall)
                            .foregroundStyle(.appOnSurfaceVariant)
                    }
                    .padding(.trailing, theme.spacing.small)
                } else {
                    Text(tbdLabel)
                        .textStyle(theme.typography.titleMedium)
                        .foregroundStyle(.appOnSurfaceVariant)
                        .padding(.trailing, theme.spacing.small)
                }
            }
            .padding(.horizontal, theme.spacing.small)
            .padding(.vertical, theme.spacing.medium)
        }
        .frame(width: DimensionConstants.cardWidth, height: DimensionConstants.cardHeight)
        .background(.appSurfaceVariant)
        .cornerRadius(theme.shapes.medium)
        .appShadow(theme.shadows.medium)
    }
}

private enum DimensionConstants {
    static let cardWidth: CGFloat = 300
    static let cardHeight: CGFloat = 120
    static let imageWidth: CGFloat = 100
    static let checkmarkSize: CGFloat = 32
    static let tapTargetSize: CGFloat = 48
}

#Preview {
    VStack(spacing: 16) {
        ContinueTrackingCard(
            episode: SwiftContinueTrackingEpisode(
                episodeId: 123,
                seasonId: 1,
                showTraktId: 1,
                episodeNumber: 3,
                seasonNumber: 2,
                episodeNumberFormatted: "S02 | E03",
                episodeTitle: "Re:start",
                imageUrl: nil,
                isWatched: false,
                daysUntilAir: nil,
                hasAired: true
            ),
            dayLabelFormat: { count in count == 1 ? "day" : "days" },
            tbdLabel: "TBD",
            onMarkWatched: {}
        )

        ContinueTrackingCard(
            episode: SwiftContinueTrackingEpisode(
                episodeId: 122,
                seasonId: 1,
                showTraktId: 1,
                episodeNumber: 2,
                seasonNumber: 2,
                episodeNumberFormatted: "S02 | E02",
                episodeTitle: "Previous Episode",
                imageUrl: nil,
                isWatched: true,
                daysUntilAir: nil,
                hasAired: true
            ),
            dayLabelFormat: { count in count == 1 ? "day" : "days" },
            tbdLabel: "TBD",
            onMarkWatched: {}
        )

        ContinueTrackingCard(
            episode: SwiftContinueTrackingEpisode(
                episodeId: 124,
                seasonId: 1,
                showTraktId: 1,
                episodeNumber: 5,
                seasonNumber: 2,
                episodeNumberFormatted: "S02 | E05",
                episodeTitle: "Upcoming Episode",
                imageUrl: nil,
                isWatched: false,
                daysUntilAir: 14,
                hasAired: false
            ),
            dayLabelFormat: { count in count == 1 ? "day" : "days" },
            tbdLabel: "TBD",
            onMarkWatched: {}
        )

        ContinueTrackingCard(
            episode: SwiftContinueTrackingEpisode(
                episodeId: 125,
                seasonId: 1,
                showTraktId: 1,
                episodeNumber: 6,
                seasonNumber: 2,
                episodeNumberFormatted: "S02 | E06",
                episodeTitle: "Unknown Air Date",
                imageUrl: nil,
                isWatched: false,
                daysUntilAir: nil,
                hasAired: false
            ),
            dayLabelFormat: { count in count == 1 ? "day" : "days" },
            tbdLabel: "TBD",
            onMarkWatched: {}
        )
    }
    .padding()
}
