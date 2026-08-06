import Components
import DesignSystem
import SwiftUI
import TvManiac

struct RatingsSectionView: View {
    @Environment(\.appTheme) private var theme

    let ratings: [SwiftRatingBar]
    let title: String

    private static let ratingLabelWidth: CGFloat = 20
    private static let countLabelMinWidth: CGFloat = 28

    var body: some View {
        CollapsibleSection(title: title) {
            VStack(spacing: theme.spacing.xSmall) {
                ForEach(ratings) { rating in
                    row(rating)
                        .testTag(StatisticsTestTags.shared.ratingRow(rating: rating.rating))
                }
            }
            .padding(.horizontal, theme.spacing.medium)
        }
        .testTag(StatisticsTestTags.shared.RATINGS_SECTION_TEST_TAG)
    }

    private func row(_ rating: SwiftRatingBar) -> some View {
        HStack(spacing: theme.spacing.small) {
            Text("\(rating.rating)")
                .textStyle(theme.typography.bodyMedium)
                .foregroundStyle(theme.colors.onSurface)
                .frame(width: Self.ratingLabelWidth, alignment: .leading)

            StatisticBar(fraction: rating.fraction)

            Text("\(rating.count)")
                .textStyle(theme.typography.bodySmall)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .frame(minWidth: Self.countLabelMinWidth, alignment: .trailing)
        }
    }
}

#Preview {
    RatingsSectionView(
        ratings: [
            .init(rating: 10, count: 12, fraction: 1.0),
            .init(rating: 9, count: 8, fraction: 0.66),
            .init(rating: 8, count: 10, fraction: 0.83),
            .init(rating: 7, count: 3, fraction: 0.25),
            .init(rating: 6, count: 1, fraction: 0.08),
        ],
        title: "Your ratings"
    )
}
