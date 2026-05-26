import DesignSystem
import SwiftUI

public struct StartWatchingListItemView: View {
    @Environment(\.appTheme) private var theme

    private let item: SwiftStartWatchingItem
    private let onItemClicked: (Int64) -> Void
    private let onShowTitleClicked: (Int64) -> Void
    private let onMarkWatched: () -> Void
    private let isUpdating: Bool

    public init(
        item: SwiftStartWatchingItem,
        onItemClicked: @escaping (Int64) -> Void,
        onShowTitleClicked: @escaping (Int64) -> Void = { _ in },
        onMarkWatched: @escaping () -> Void = {},
        isUpdating: Bool = false
    ) {
        self.item = item
        self.onItemClicked = onItemClicked
        self.onShowTitleClicked = onShowTitleClicked
        self.onMarkWatched = onMarkWatched
        self.isUpdating = isUpdating
    }

    public var body: some View {
        HStack(alignment: .top, spacing: 0) {
            posterView

            if item.hasEpisode {
                episodeDetails
                watchedButton
            } else {
                showDetails
            }
        }
        .frame(height: StartWatchingListItemViewConstants.height)
        .frame(maxWidth: .infinity)
        .background(.appSurface)
        .cornerRadius(StartWatchingListItemViewConstants.cornerRadius)
        .contentShape(Rectangle())
        .onTapGesture { onItemClicked(item.traktId) }
        .padding(.horizontal, theme.spacing.medium)
    }

    private var posterView: some View {
        PosterItemView(
            title: nil,
            posterUrl: item.stillUrl ?? item.posterUrl,
            posterWidth: StartWatchingListItemViewConstants.imageWidth,
            posterHeight: StartWatchingListItemViewConstants.height,
            posterRadius: 0
        )
        .frame(width: StartWatchingListItemViewConstants.imageWidth, height: StartWatchingListItemViewConstants.height)
        .clipped()
    }

    private var episodeDetails: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            TextTitlePill(
                title: item.title,
                onTap: { onShowTitleClicked(item.traktId) }
            )

            if let episodeNumber = item.episodeNumber {
                Text(episodeInfoText(episodeNumber))
                    .textStyle(theme.typography.bodySmall)
                    .foregroundStyle(.appAccent)
                    .lineLimit(1)
                    .padding(.top, theme.spacing.xSmall)
            }

            if let episodeTitle = item.episodeTitle {
                Text(episodeTitle)
                    .textStyle(theme.typography.bodySmall)
                    .foregroundStyle(.appOnSurface.opacity(0.7))
                    .lineLimit(2)
            }

            Spacer()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, theme.spacing.small)
        .padding(.horizontal, theme.spacing.xSmall)
    }

    private var showDetails: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            TextTitlePill(
                title: item.title,
                onTap: { onShowTitleClicked(item.traktId) }
            )

            if let year = item.year {
                Text(year)
                    .textStyle(theme.typography.bodySmall)
                    .foregroundStyle(.appOnSurfaceVariant)
            }

            Spacer()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, theme.spacing.small)
        .padding(.horizontal, theme.spacing.xSmall)
    }

    private var watchedButton: some View {
        Button(action: onMarkWatched) {
            ZStack {
                Circle()
                    .fill(.appGrey)
                    .frame(
                        width: StartWatchingListItemViewConstants.checkmarkSize,
                        height: StartWatchingListItemViewConstants.checkmarkSize
                    )
                if isUpdating {
                    ProgressView()
                        .progressViewStyle(.circular)
                        .tint(theme.colors.onPrimary)
                } else {
                    Image(systemName: "checkmark")
                        .font(theme.typography.titleSmall)
                        .foregroundStyle(.white)
                }
            }
            .frame(
                width: StartWatchingListItemViewConstants.tapTargetSize,
                height: StartWatchingListItemViewConstants.height
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(isUpdating)
        .frame(maxHeight: .infinity)
        .padding(.trailing, theme.spacing.small)
    }

    private func episodeInfoText(_ episodeNumber: String) -> String {
        var text = episodeNumber
        if let runtime = item.runtime {
            text += " (\(runtime))"
        }
        return text
    }
}

private enum StartWatchingListItemViewConstants {
    static let height: CGFloat = 140
    static let imageWidth: CGFloat = 120
    static let cornerRadius: CGFloat = 2
    static let checkmarkSize: CGFloat = 36
    static let tapTargetSize: CGFloat = 48
}

#Preview {
    VStack(spacing: 8) {
        StartWatchingListItemView(
            item: SwiftStartWatchingItem(
                traktId: 1,
                title: "Breaking Bad",
                posterUrl: nil,
                year: "2008",
                episodeId: 11,
                episodeTitle: "Pilot",
                episodeNumber: "S01 | E01",
                seasonNumber: 1,
                episodeNumberValue: 1,
                runtime: "58 min"
            ),
            onItemClicked: { _ in }
        )

        StartWatchingListItemView(
            item: SwiftStartWatchingItem(
                traktId: 2,
                title: "Severance: A Very Long Title That Should Wrap To Two Lines",
                posterUrl: nil,
                year: "2022"
            ),
            onItemClicked: { _ in }
        )
    }
    .appPreview()
}
