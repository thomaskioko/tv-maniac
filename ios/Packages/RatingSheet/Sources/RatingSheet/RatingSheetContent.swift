import Components
import DesignSystem
import SwiftUI

public struct RatingSheetContent: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.hapticFeedbackEnabled) private var hapticFeedbackEnabled

    private let headerLabel: String
    private let title: String
    private let subtitle: String?
    private let posterUrl: String?
    private let backdropUrl: String?
    private let scoreLabel: String
    private let removeLabel: String
    private let userRating: Int?
    private let onRatingSelected: (Int) -> Void
    private let onRemove: () -> Void

    public init(
        headerLabel: String,
        title: String,
        subtitle: String?,
        posterUrl: String? = nil,
        backdropUrl: String? = nil,
        scoreLabel: String,
        removeLabel: String,
        userRating: Int?,
        onRatingSelected: @escaping (Int) -> Void,
        onRemove: @escaping () -> Void
    ) {
        self.headerLabel = headerLabel
        self.title = title
        self.subtitle = subtitle
        self.posterUrl = posterUrl
        self.backdropUrl = backdropUrl
        self.scoreLabel = scoreLabel
        self.removeLabel = removeLabel
        self.userRating = userRating
        self.onRatingSelected = onRatingSelected
        self.onRemove = onRemove
    }

    public var body: some View {
        VStack(spacing: 0) {
            grabber

            VStack(alignment: .leading, spacing: theme.spacing.large) {
                header

                VStack(alignment: .leading, spacing: theme.spacing.small) {
                    Text(scoreLabel)
                        .textStyle(theme.typography.titleMedium)
                        .foregroundStyle(.appOnSurface)

                    scoreGrid
                }

                if userRating != nil {
                    removeButton
                        .frame(maxWidth: .infinity)
                }
            }
            .padding(.horizontal, theme.spacing.medium)
            .padding(.top, theme.spacing.small)
        }
        .padding(.bottom, theme.spacing.large)
        .frame(maxWidth: .infinity)
        .background(.appSurface)
        .clipShape(.rect(topLeadingRadius: sheetCornerRadius, topTrailingRadius: sheetCornerRadius))
    }

    private var grabber: some View {
        RoundedRectangle(cornerRadius: 2.5)
            .fill(theme.colors.onSurface.opacity(0.4))
            .frame(width: 36, height: 5)
            .frame(maxWidth: .infinity, alignment: .center)
            .padding(.vertical, theme.spacing.small)
    }

    private var header: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
            Text(headerLabel)
                .textCase(.uppercase)
                .textStyle(theme.typography.labelMedium)
                .foregroundStyle(.appSecondary)

            HStack(alignment: .top, spacing: theme.spacing.small) {
                if let backdropUrl {
                    targetImage(url: backdropUrl, imageType: .backdrop, aspectRatio: ImageDimens.backdropAspect)
                } else if let posterUrl {
                    targetImage(url: posterUrl, imageType: .poster, aspectRatio: ImageDimens.posterAspect)
                }

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(title)
                        .textStyle(theme.typography.headlineSmall)
                        .foregroundStyle(.appOnSurface)

                    if let subtitle {
                        Text(subtitle)
                            .textStyle(theme.typography.bodyMedium)
                            .foregroundStyle(.appOnSurfaceVariant)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    private func targetImage(url: String, imageType: TmdbImageType, aspectRatio: CGFloat) -> some View {
        LazyResizableImage(
            url: url,
            imageType: imageType,
            size: CGSize(width: headerImageHeight * aspectRatio, height: headerImageHeight)
        )
        .frame(width: headerImageHeight * aspectRatio, height: headerImageHeight)
        .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
    }

    private var scoreGrid: some View {
        VStack(spacing: theme.spacing.xSmall) {
            ForEach(scoreRows, id: \.self) { row in
                HStack(spacing: theme.spacing.xSmall) {
                    ForEach(row, id: \.self) { value in
                        scoreTile(value: value)
                    }
                }
            }
        }
    }

    private func scoreTile(value: Int) -> some View {
        let isSelected = userRating == value
        return Button(action: {
            Haptics.impact(isEnabled: hapticFeedbackEnabled)
            onRatingSelected(value)
        }, label: {
            Text("\(value)")
                .textStyle(theme.typography.titleMedium)
                .foregroundStyle(isSelected ? AnyShapeStyle(.appOnSecondary) : AnyShapeStyle(.appOnSurface))
                .frame(maxWidth: .infinity, minHeight: scoreTileHeight)
                .background(isSelected ? AnyShapeStyle(.appSecondary) : AnyShapeStyle(.appSurfaceVariant))
                .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
                .overlay(
                    RoundedRectangle(cornerRadius: theme.shapes.medium)
                        .stroke(
                            isSelected ? AnyShapeStyle(Color.clear) : AnyShapeStyle(.appOnSurface.opacity(unselectedTileBorderOpacity)),
                            lineWidth: 1
                        )
                )
        })
        .buttonStyle(.plain)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }

    private var removeButton: some View {
        Button(action: onRemove) {
            HStack(spacing: theme.spacing.xSmall) {
                Image(systemName: "trash")
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(.appOnButtonBackground)

                Text(removeLabel)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(.appOnButtonBackground)
            }
            .padding(.horizontal, theme.spacing.medium)
            .padding(.vertical, theme.spacing.xSmall)
            .background(.appError.opacity(0.65), in: Capsule())
        }
        .buttonStyle(PlainButtonStyle())
    }

    private var scoreRows: [[Int]] {
        stride(from: 1, through: maxScore, by: scoresPerRow).map { start in
            Array(start ... min(start + scoresPerRow - 1, maxScore))
        }
    }
}

private let maxScore = 10
private let scoresPerRow = 5
private let scoreTileHeight: CGFloat = 56
private let headerImageHeight: CGFloat = 72
private let unselectedTileBorderOpacity = 0.8
private let sheetCornerRadius: CGFloat = 16

#Preview("Unrated") {
    RatingSheetContent(
        headerLabel: "You're rating",
        title: "Lioness",
        subtitle: "2023",
        posterUrl: "/lioness.jpg",
        scoreLabel: "Your rating",
        removeLabel: "Remove rating",
        userRating: nil,
        onRatingSelected: { _ in },
        onRemove: {}
    )
    .appPreview()
}

#Preview("Rated") {
    RatingSheetContent(
        headerLabel: "You're rating",
        title: "Sacrificial Soldiers",
        subtitle: "Lioness • S1E1",
        backdropUrl: "/sacrificial-soldiers.jpg",
        scoreLabel: "Your rating",
        removeLabel: "Remove rating",
        userRating: 8,
        onRatingSelected: { _ in },
        onRemove: {}
    )
    .appPreview()
}
