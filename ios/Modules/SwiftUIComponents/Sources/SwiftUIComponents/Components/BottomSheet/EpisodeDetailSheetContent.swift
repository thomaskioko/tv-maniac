import DesignSystem
import SwiftUI

public struct EpisodeDetailInfo: Equatable {
    public let title: String
    public let imageUrl: String?
    public let episodeInfo: String
    public let overview: String?
    public let rating: Double?
    public let voteCount: Int64?

    public init(
        title: String,
        imageUrl: String?,
        episodeInfo: String,
        overview: String? = nil,
        rating: Double? = nil,
        voteCount: Int64? = nil
    ) {
        self.title = title
        self.imageUrl = imageUrl
        self.episodeInfo = episodeInfo
        self.overview = overview
        self.rating = rating
        self.voteCount = voteCount
    }
}

public struct EpisodeDetailSheetContent<Actions: View>: View {
    @Environment(\.appTheme) private var theme

    private let episode: EpisodeDetailInfo
    private let actions: (() -> Actions)?

    public init(
        episode: EpisodeDetailInfo,
        @ViewBuilder actions: @escaping () -> Actions
    ) {
        self.episode = episode
        self.actions = actions
    }

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                headerImage

                VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
                    Text(episode.title)
                        .textStyle(theme.typography.titleLarge)
                        .foregroundStyle(.appOnSurface)

                    Text(episode.episodeInfo)
                        .textStyle(theme.typography.bodyMedium)
                        .foregroundStyle(.appOnSurfaceVariant)

                    ratingRow

                    if let overview = episode.overview, !overview.isEmpty {
                        Text(overview)
                            .textStyle(theme.typography.bodyMedium)
                            .foregroundStyle(.appOnSurface)
                            .padding(.top, theme.spacing.xxSmall)
                    }
                }
                .padding(.horizontal, theme.spacing.medium)
                .padding(.top, theme.spacing.small)

                if let actions {
                    Divider()
                        .padding(.top, theme.spacing.medium)

                    actions()
                }
            }
        }
        .scrollBounceBehavior(.basedOnSize)
        .background(.appSurface)
    }

    private var headerImage: some View {
        ZStack(alignment: .top) {
            LazyResizableImage(
                url: episode.imageUrl,
                imageType: .backdrop,
                size: CGSize(width: UIScreen.main.bounds.width, height: 280)
            )
            .frame(maxWidth: .infinity, maxHeight: 280)
            .clipped()

            RoundedRectangle(cornerRadius: 2)
                .fill(theme.colors.onSurface.opacity(0.6))
                .frame(width: 32, height: 4)
                .padding(.top, theme.spacing.xSmall)
        }
    }

    @ViewBuilder
    private var ratingRow: some View {
        if let rating = episode.rating {
            HStack(spacing: theme.spacing.xxSmall) {
                Image(systemName: "star.fill")
                    .font(theme.typography.labelMedium)
                    .foregroundStyle(.appAccent)

                Text(buildRatingText(rating: rating))
                    .textStyle(theme.typography.bodySmall)
                    .foregroundStyle(.appOnSurfaceVariant)
            }
        }
    }

    private func buildRatingText(rating: Double) -> String {
        var text = String(format: "%.1f", rating)
        if let voteCount = episode.voteCount {
            text += " (\(voteCount) votes)"
        }
        return text
    }
}

public extension EpisodeDetailSheetContent where Actions == EmptyView {
    init(episode: EpisodeDetailInfo) {
        self.episode = episode
        actions = nil
    }
}

public struct SheetActionItem: View {
    @Environment(\.appTheme) private var theme

    private let icon: String
    private let label: String
    private let action: () -> Void

    public init(icon: String, label: String, action: @escaping () -> Void) {
        self.icon = icon
        self.label = label
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: theme.spacing.medium) {
                Image(systemName: icon)
                    .font(theme.typography.titleMedium)
                    .foregroundStyle(.appOnSurface)
                    .frame(width: 24)

                Text(label)
                    .textStyle(theme.typography.bodyLarge)
                    .foregroundStyle(.appOnSurface)

                Spacer()
            }
            .padding(.horizontal, theme.spacing.medium)
            .padding(.vertical, theme.spacing.small)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

#Preview("Detail Only") {
    EpisodeDetailSheetContent(
        episode: EpisodeDetailInfo(
            title: "The Walking Dead: Daryl Dixon",
            imageUrl: nil,
            episodeInfo: "S02E01 \u{2022} 45m",
            overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
            rating: 8.5,
            voteCount: 1234
        )
    )
    .appPreview()
}

#Preview("With Actions") {
    EpisodeDetailSheetContent(
        episode: EpisodeDetailInfo(
            title: "Wednesday",
            imageUrl: nil,
            episodeInfo: "S02E03 \u{2022} 50m",
            overview: "Wednesday arrives at Nevermore Academy and begins investigating a series of mysterious events.",
            rating: 7.9,
            voteCount: 856
        )
    ) {
        SheetActionItem(icon: "checkmark.circle", label: "Mark as Watched", action: {})
        SheetActionItem(icon: "tv", label: "Open Show", action: {})
        SheetActionItem(icon: "list.bullet", label: "Open Season", action: {})
        SheetActionItem(icon: "minus.circle", label: "Unfollow Show", action: {})
    }
    .appPreview()
}

#Preview("No Rating") {
    EpisodeDetailSheetContent(
        episode: EpisodeDetailInfo(
            title: "House of the Dragon",
            imageUrl: nil,
            episodeInfo: "S03E01",
            overview: "King Viserys hosts a tournament to celebrate the birth of his heir."
        )
    )
    .appPreview()
}
