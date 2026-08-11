import Components
import DesignSystem
import SwiftUI
import TvManiac

struct WatchTimeHeroView: View {
    @Environment(\.appTheme) private var theme

    let watchTime: SwiftWatchTime
    let title: String
    let daysLabel: String
    let hoursLabel: String
    let minutesLabel: String

    var body: some View {
        CollapsibleSection(title: title) {
            HStack(spacing: theme.spacing.large) {
                segment(value: watchTime.days, label: daysLabel)
                segment(value: watchTime.hours, label: hoursLabel)
                segment(value: watchTime.minutes, label: minutesLabel)
            }
            .padding(theme.spacing.medium)
            .frame(maxWidth: .infinity)
            .background(theme.colors.surface)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.large))
            .appShadow(theme.shadows.small)
            .padding(.horizontal, theme.spacing.medium)
        }
        .testTag(StatisticsTestTags.shared.WATCH_TIME_HERO_TEST_TAG)
    }

    private func segment(value: Int64, label: String) -> some View {
        VStack(spacing: theme.spacing.xxxSmall) {
            Text("\(value)")
                .textStyle(theme.typography.displaySmall)
                .fontWeight(.bold)
                .foregroundStyle(theme.colors.accent)

            Text(label)
                .textStyle(theme.typography.labelSmall)
                .tracking(sectionLabelLetterSpacing)
                .foregroundStyle(theme.colors.onSurfaceVariant)
        }
        .frame(maxWidth: .infinity)
    }
}

#Preview {
    WatchTimeHeroView(
        watchTime: SwiftWatchTime(days: 12, hours: 4, minutes: 30),
        title: "Total time watched",
        daysLabel: "days",
        hoursLabel: "hours",
        minutesLabel: "minutes"
    )
}
