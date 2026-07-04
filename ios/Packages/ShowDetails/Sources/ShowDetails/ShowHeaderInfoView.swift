import Components
import DesignSystem
import Models
import SwiftUI

public struct ShowHeaderInfoView: View {
    @Environment(\.appTheme) private var theme

    private let title: String
    private let overview: String
    private let status: String?
    private let year: String
    private let language: String?
    private let communityRating: Double?
    private let communityVotes: Int64?
    private let seasonCount: Int
    private let seasonCountFormat: (_ count: Int) -> String

    public init(
        title: String,
        overview: String,
        status: String?,
        year: String,
        language: String?,
        communityRating: Double?,
        communityVotes: Int64?,
        seasonCount: Int,
        seasonCountFormat: @escaping (_ count: Int) -> String
    ) {
        self.title = title
        self.overview = overview
        self.status = status
        self.year = year
        self.language = language
        self.communityRating = communityRating
        self.communityVotes = communityVotes
        self.seasonCount = seasonCount
        self.seasonCountFormat = seasonCountFormat
    }

    public var body: some View {
        VStack(spacing: 0) {
            Text(title)
                .textStyle(theme.typography.headlineLarge)
                .foregroundStyle(.appOnSurface)
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
                    .foregroundStyle(.appAccent)
            }

            Text(year)
                .textStyle(theme.typography.bodyMedium)

            if seasonCount > 0 {
                Text("•")
                    .textStyle(theme.typography.labelSmall)
                    .foregroundStyle(.appAccent)

                Text(seasonCountFormat(seasonCount))
                    .textStyle(theme.typography.bodyMedium)
            }

            if let language {
                Text("•")
                    .textStyle(theme.typography.labelSmall)
                    .foregroundStyle(.appAccent)

                Text(language)
                    .textStyle(theme.typography.bodyMedium)
            }

            if let communityRating {
                Text("•")
                    .textStyle(theme.typography.labelSmall)
                    .foregroundStyle(.appAccent)

                Image(systemName: "star.fill")
                    .resizable()
                    .frame(width: 14, height: 14)
                    .foregroundStyle(.appAccent)

                Text(String(format: "%.1f", communityRating))
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(.appAccent)
                    .accessibilityLabel(communityRatingAccessibilityLabel(communityRating))
            }
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }

    private func communityRatingAccessibilityLabel(_ communityRating: Double) -> String {
        guard let communityVotes else {
            return String(format: "%.1f", communityRating)
        }
        return "\(String(format: "%.1f", communityRating)) (\(communityVotes) votes)"
    }
}

#Preview("Community rating") {
    VStack {
        ShowHeaderInfoView(
            title: "Arcane",
            overview: "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
            status: "Ended",
            year: "2024",
            language: "EN",
            communityRating: 4.8,
            communityVotes: 12500,
            seasonCount: 2,
            seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" }
        )
    }
    .appPreview()
}

#Preview("No community rating") {
    VStack {
        ShowHeaderInfoView(
            title: "Arcane",
            overview: "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
            status: "Ended",
            year: "2024",
            language: "EN",
            communityRating: nil,
            communityVotes: nil,
            seasonCount: 2,
            seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" }
        )
    }
    .appPreview()
}
