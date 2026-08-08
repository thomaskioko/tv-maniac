import Components
import DesignSystem
import SwiftUI
import TvManiac

struct ActivityChartSectionView: View {
    @Environment(\.appTheme) private var theme
    @SwiftUI.State private var selectedLabel: String?

    let bars: [SwiftActivityBar]
    let title: String
    let sectionTestTag: String
    let sectionName: String
    var showAxisLabels: Bool = false

    private static let chartHeight: CGFloat = 120
    private static let barCorner: CGFloat = 4
    private static let tooltipCorner: CGFloat = 8
    private static let tooltipBorderAlpha: Double = 0.3
    private static let minBarFraction: Float = 0.02
    private static let dimmestBarAlpha: Double = 0.45
    private static let axisLabelCount = 4

    var body: some View {
        VStack(spacing: 0) {
            ZStack(alignment: .top) {
                HStack(alignment: .bottom, spacing: theme.spacing.xxxSmall) {
                    ForEach(bars) { bar in
                        barColumn(bar)
                    }
                }
                .frame(height: Self.chartHeight)

                if let selected = bars.first(where: { $0.label == selectedLabel }) {
                    tooltip(selected)
                }
            }

            if showAxisLabels {
                axisLabels
                    .padding(.top, theme.spacing.xxSmall)
            }

            Text(title.uppercased())
                .textStyle(theme.typography.labelMedium)
                .tracking(sectionLabelLetterSpacing)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .multilineTextAlignment(.center)
                .padding(.top, theme.spacing.small)
        }
        .padding(.horizontal, theme.spacing.medium)
        .screenTag(sectionTestTag)
    }

    private func barColumn(_ bar: SwiftActivityBar) -> some View {
        let isSelected = bar.label == selectedLabel
        let strength = isSelected
            ? 1
            : Self.dimmestBarAlpha + (1 - Self.dimmestBarAlpha) * Double(bar.fraction)
        let height = Self.chartHeight * CGFloat(min(max(bar.fraction, Self.minBarFraction), 1))

        return VStack(spacing: 0) {
            Spacer(minLength: 0)

            RoundedRectangle(cornerRadius: Self.barCorner)
                .fill(theme.colors.accent.opacity(strength))
                .frame(height: height)
        }
        .frame(maxWidth: .infinity)
        .contentShape(Rectangle())
        .onTapGesture { selectedLabel = isSelected ? nil : bar.label }
        .accessibilityElement()
        .accessibilityLabel("\(bar.label), \(bar.caption)")
        .accessibilityAddTraits(.isButton)
        .accessibilityAction { selectedLabel = isSelected ? nil : bar.label }
        .testTag(StatisticsTestTags.shared.activityBar(section: sectionName, label: bar.label))
    }

    private func tooltip(_ bar: SwiftActivityBar) -> some View {
        VStack(spacing: 0) {
            Text(bar.caption)
                .textStyle(theme.typography.titleMedium)
                .foregroundStyle(theme.colors.onSurface)

            Text(bar.label)
                .textStyle(theme.typography.bodyMedium)
                .foregroundStyle(theme.colors.onSurfaceVariant)
        }
        .padding(.horizontal, theme.spacing.medium)
        .padding(.vertical, theme.spacing.small)
        .background(
            RoundedRectangle(cornerRadius: Self.tooltipCorner)
                .fill(theme.colors.surface)
        )
        .overlay(
            RoundedRectangle(cornerRadius: Self.tooltipCorner)
                .stroke(
                    theme.colors.onSurfaceVariant.opacity(Self.tooltipBorderAlpha),
                    lineWidth: 1
                )
        )
        .testTag(StatisticsTestTags.shared.ACTIVITY_TOOLTIP_TEST_TAG)
    }

    private var axisLabels: some View {
        HStack(spacing: 0) {
            ForEach(labelledBars) { bar in
                Text(bar.label)
                    .textStyle(theme.typography.labelSmall)
                    .foregroundStyle(theme.colors.onSurfaceVariant)
                    .lineLimit(1)
                    .fixedSize()

                if bar.id != labelledBars.last?.id {
                    Spacer(minLength: theme.spacing.xxSmall)
                }
            }
        }
        .frame(maxWidth: .infinity)
    }

    private var labelledBars: [SwiftActivityBar] {
        let lastIndex = bars.count - 1
        guard lastIndex > 0 else { return Array(bars.prefix(1)) }

        let indices = Set((0 ..< Self.axisLabelCount).map { $0 * lastIndex / (Self.axisLabelCount - 1) })
        return bars.enumerated().filter { indices.contains($0.offset) }.map(\.element)
    }
}

#Preview {
    ActivityChartSectionView(
        bars: [
            .init(label: "Mon", caption: "12 episodes", fraction: 0.6),
            .init(label: "Tue", caption: "8 episodes", fraction: 0.4),
            .init(label: "Wed", caption: "14 episodes", fraction: 0.7),
            .init(label: "Thu", caption: "6 episodes", fraction: 0.3),
            .init(label: "Fri", caption: "17 episodes", fraction: 0.85),
            .init(label: "Sat", caption: "20 episodes", fraction: 1),
            .init(label: "Sun", caption: "0 episodes", fraction: 0),
        ],
        title: "Episodes by day of the week",
        sectionTestTag: StatisticsTestTags.shared.WEEKDAY_ACTIVITY_TEST_TAG,
        sectionName: "weekday"
    )
}
