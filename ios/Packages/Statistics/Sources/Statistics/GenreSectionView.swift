import Components
import DesignSystem
import SwiftUI
import TvManiac

struct GenreSectionView: View {
    @Environment(\.appTheme) private var theme

    let genres: [SwiftGenreSlice]
    let title: String
    let emptyMessage: String

    var body: some View {
        CollapsibleSection(title: title) {
            VStack(spacing: theme.spacing.small) {
                if genres.isEmpty {
                    Text(emptyMessage)
                        .textStyle(theme.typography.bodyMedium)
                        .foregroundStyle(theme.colors.onSurfaceVariant)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .screenTag(StatisticsTestTags.shared.GENRES_EMPTY_MESSAGE_TEST_TAG)
                } else {
                    ForEach(genres) { genre in
                        row(genre)
                            .testTag(StatisticsTestTags.shared.genreRow(name: genre.name))
                    }
                }
            }
            .padding(.horizontal, theme.spacing.medium)
        }
        .screenTag(StatisticsTestTags.shared.GENRES_SECTION_TEST_TAG)
    }

    private func row(_ genre: SwiftGenreSlice) -> some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxxSmall) {
            HStack {
                Text(genre.name)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(theme.colors.onSurface)

                Spacer()

                Text(genre.caption)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(theme.colors.onSurfaceVariant)
            }

            StatisticBar(fraction: genre.fraction)
        }
    }
}

#Preview {
    GenreSectionView(
        genres: [
            .init(name: "Drama", showCount: 24, caption: "24 shows", fraction: 1),
            .init(name: "Comedy", showCount: 11, caption: "11 shows", fraction: 0.45),
            .init(name: "Crime", showCount: 9, caption: "9 shows", fraction: 0.37),
            .init(name: "Science Fiction", showCount: 7, caption: "7 shows", fraction: 0.29),
            .init(name: "Other", showCount: 12, caption: "12 shows", fraction: 0.5),
        ],
        title: "Genres you watch",
        emptyMessage: ""
    )
}

#Preview("No genres") {
    GenreSectionView(
        genres: [],
        title: "Genres you watch",
        emptyMessage: "We do not know the genres of the shows you have watched yet."
    )
}
