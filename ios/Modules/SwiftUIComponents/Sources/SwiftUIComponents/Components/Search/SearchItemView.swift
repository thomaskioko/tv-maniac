import DesignSystem
import SwiftUI

public struct SearchItemView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    private let title: String
    private let overview: String?
    private let imageUrl: String?
    private let status: String?
    private let year: String?
    private let voteAverage: Double?

    public init(
        title: String,
        overview: String?,
        imageUrl: String?,
        status: String?,
        year: String?,
        voteAverage: Double?
    ) {
        self.title = title
        self.overview = overview
        self.imageUrl = imageUrl
        self.status = status
        self.year = year
        self.voteAverage = voteAverage
    }

    public var body: some View {
        HStack(alignment: .top, spacing: theme.spacing.xxxSmall) {
            PosterItemView(
                title: nil,
                posterUrl: imageUrl,
                posterWidth: ImageType.poster.width(widthSizeClass),
                aspectRatio: ImageType.poster.aspect,
                posterRadius: 0
            )

            VStack(alignment: .leading, spacing: theme.spacing.xxxSmall) {
                Text(title)
                    .textStyle(theme.typography.titleMedium)
                    .foregroundStyle(.appOnSurface)
                    .lineLimit(1)
                    .padding(.top, theme.spacing.xSmall)

                HStack(spacing: theme.spacing.xxxSmall) {
                    if let voteAverage {
                        Image(systemName: "star")
                            .foregroundStyle(.appSecondary)
                            .textStyle(theme.typography.bodySmall)

                        Text(String(format: "%.1f", voteAverage))
                            .textStyle(theme.typography.bodySmall)
                            .foregroundStyle(.appOnSurface)

                        Text("•")
                            .textStyle(theme.typography.labelSmall)
                            .foregroundStyle(.appSecondary)
                    }

                    if let year {
                        Text(year)
                            .textStyle(theme.typography.bodySmall)
                            .foregroundStyle(.appOnSurface)

                        Text("•")
                            .textStyle(theme.typography.labelSmall)
                            .foregroundStyle(.appSecondary)
                    }

                    if let status, !status.isEmpty {
                        Text(status)
                            .textStyle(theme.typography.labelMedium)
                            .foregroundStyle(.appSecondary)
                            .padding(.horizontal, theme.spacing.xxxSmall)
                            .background(.appSecondary.opacity(0.08))
                    }
                }
                .padding(.vertical, theme.spacing.xxxSmall)

                if let overview, !overview.isEmpty {
                    Text(overview)
                        .textStyle(theme.typography.labelSmall)
                        .foregroundStyle(.appOnSurface)
                        .lineLimit(2)
                        .padding(.vertical, theme.spacing.xxxSmall)
                }
            }
            .padding(.vertical, theme.spacing.xxxSmall)
            .padding(.horizontal, theme.spacing.xSmall)

            Spacer()
        }
        .frame(maxWidth: .infinity)
        .background(.appSurface)
        .cornerRadius(theme.shapes.small)
        .appShadow(theme.shadows.medium)
    }
}

#Preview {
    VStack(spacing: 16) {
        SearchItemView(
            title: "Loki",
            overview: "After stealing the Tesseract during the events of Avengers: Endgame, an alternate version of Loki is brought to the mysterious Time Variance Authority.",
            imageUrl: "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            status: "Ended",
            year: "2012",
            voteAverage: 6.0
        )

        SearchItemView(
            title: "Arcane",
            overview: nil,
            imageUrl: "/8rjILRAlcvI9y7vJuH9yNjKYhta.jpg",
            status: "Returning Series",
            year: "2024",
            voteAverage: 8.9
        )
    }
    .padding()
}
