import SwiftUI

public struct SearchItemView: View {
    @Theme private var theme

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
                posterWidth: 100,
                posterHeight: 125,
                posterRadius: 0
            )

            VStack(alignment: .leading, spacing: theme.spacing.xxxSmall) {
                Text(title)
                    .textStyle(theme.typography.titleMedium)
                    .fontWeight(.medium)
                    .foregroundColor(theme.colors.onSurface)
                    .lineLimit(1)
                    .padding(.top, theme.spacing.xSmall)

                HStack(spacing: theme.spacing.xxxSmall) {
                    if let voteAverage {
                        Image(systemName: "star")
                            .foregroundColor(theme.colors.secondary)
                            .font(.system(size: 12))

                        Text(String(format: "%.1f", voteAverage))
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurface)

                        Text("•")
                            .textStyle(theme.typography.labelSmall)
                            .foregroundColor(theme.colors.secondary)
                            .font(.system(size: 8))
                    }

                    if let year {
                        Text(year)
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurface)

                        Text("•")
                            .textStyle(theme.typography.labelSmall)
                            .foregroundColor(theme.colors.secondary)
                            .font(.system(size: 8))
                    }

                    if let status, !status.isEmpty {
                        Text(status)
                            .textStyle(theme.typography.labelMedium)
                            .fontWeight(.regular)
                            .foregroundColor(theme.colors.secondary)
                            .padding(.horizontal, theme.spacing.xxxSmall)
                            .background(theme.colors.secondary.opacity(0.08))
                    }
                }
                .padding(.vertical, theme.spacing.xxxSmall)

                if let overview, !overview.isEmpty {
                    Text(overview)
                        .textStyle(theme.typography.labelSmall)
                        .fontWeight(.regular)
                        .foregroundColor(theme.colors.onSurface)
                        .lineLimit(2)
                        .padding(.vertical, theme.spacing.xxxSmall)
                }
            }
            .padding(.vertical, theme.spacing.xxxSmall)
            .padding(.horizontal, theme.spacing.xSmall)

            Spacer()
        }
        .frame(maxWidth: .infinity)
        .background(theme.colors.surface)
        .cornerRadius(theme.shapes.small)
        .shadow(color: .black.opacity(0.1), radius: 4, x: 0, y: 2)
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
