import SwiftUI

public struct ShowHeaderInfoView: View {
    @Theme private var theme

    private let title: String
    private let overview: String
    private let status: String?
    private let year: String
    private let language: String?
    private let rating: Double

    public init(
        title: String,
        overview: String,
        status: String?,
        year: String,
        language: String?,
        rating: Double
    ) {
        self.title = title
        self.overview = overview
        self.status = status
        self.year = year
        self.language = language
        self.rating = rating
    }

    public var body: some View {
        VStack(spacing: 0) {
            Text(title)
                .textStyle(theme.typography.headlineLarge)
                .foregroundColor(theme.colors.onSurface)
                .lineLimit(1)
                .padding(.horizontal, theme.spacing.medium)
                .frame(maxWidth: .infinity, alignment: .center)

            showDetailMetadata
                .padding(.horizontal, theme.spacing.medium)
                .padding(.vertical, theme.spacing.xSmall)

            OverviewBoxView(overview: overview)
                .padding(.horizontal, theme.spacing.medium)
                .padding(.bottom, theme.spacing.small)
        }
    }

    private var showDetailMetadata: some View {
        HStack(alignment: .center) {
            if let status, !status.isEmpty {
                BorderTextView(
                    text: status,
                    colorOpacity: 0.12,
                    borderOpacity: 0.12,
                    weight: .bold
                )

                Text("•")
                    .textStyle(theme.typography.labelSmall)
                    .foregroundColor(theme.colors.accent)
            }

            Text(year)
                .textStyle(theme.typography.bodyMedium)

            if let language {
                Text("•")
                    .textStyle(theme.typography.labelSmall)
                    .foregroundColor(theme.colors.accent)

                Text(language)
                    .textStyle(theme.typography.bodyMedium)
            }

            Text("•")
                .textStyle(theme.typography.labelSmall)
                .foregroundColor(theme.colors.accent)

            Image(systemName: "star.fill")
                .resizable()
                .frame(width: 14, height: 14)
                .foregroundColor(theme.colors.accent)

            Text(String(format: "%.1f", rating))
                .textStyle(theme.typography.bodyMedium)

            Text("•")
                .textStyle(theme.typography.labelSmall)
                .foregroundColor(theme.colors.accent)
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }
}

#Preview {
    VStack {
        ShowHeaderInfoView(
            title: "Arcane",
            overview: "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
            status: "Ended",
            year: "2024",
            language: "EN",
            rating: 4.8
        )
    }
    .themedPreview()
}
