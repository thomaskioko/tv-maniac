import SwiftUI

public struct SeasonProgressCard: View {
    @Theme private var theme

    private let season: SwiftSeason
    private let isSelected: Bool
    private let onClick: () -> Void

    public init(
        season: SwiftSeason,
        isSelected: Bool,
        onClick: @escaping () -> Void
    ) {
        self.season = season
        self.isSelected = isSelected
        self.onClick = onClick
    }

    public var body: some View {
        ChipView(
            label: season.name,
            isSelected: isSelected,
            action: onClick
        )
    }
}

#Preview {
    VStack(spacing: 16) {
        SeasonProgressCard(
            season: SwiftSeason(
                tvShowId: 1,
                seasonId: 1,
                seasonNumber: 1,
                name: "Season 1",
                watchedCount: 4,
                totalCount: 6,
                progressPercentage: 0.67
            ),
            isSelected: false,
            onClick: {}
        )

        SeasonProgressCard(
            season: SwiftSeason(
                tvShowId: 1,
                seasonId: 2,
                seasonNumber: 2,
                name: "Season 2",
                watchedCount: 6,
                totalCount: 6,
                progressPercentage: 1.0
            ),
            isSelected: true,
            onClick: {}
        )
    }
    .padding()
    .environment(\.tvManiacTheme, DarkTheme())
}
