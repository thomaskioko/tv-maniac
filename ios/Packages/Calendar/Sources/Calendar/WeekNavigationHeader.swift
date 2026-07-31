import DesignSystem
import SwiftUI

struct WeekNavigationHeader: View {
    @Environment(\.appTheme) private var theme

    let weekLabel: String
    let canNavigatePrevious: Bool
    let canNavigateNext: Bool
    let isRefreshing: Bool
    let onPreviousWeek: () -> Void
    let onNextWeek: () -> Void

    var body: some View {
        HStack {
            Button(action: onPreviousWeek) {
                Image(systemName: "chevron.left")
                    .foregroundStyle(canNavigatePrevious
                        ? AnyShapeStyle(.appOnSurface)
                        : AnyShapeStyle(.appOnSurface.opacity(0.3)))
            }
            .disabled(!canNavigatePrevious)

            Spacer()

            HStack(spacing: theme.spacing.xSmall) {
                Text(weekLabel)
                    .textStyle(theme.typography.titleSmall)
                    .foregroundStyle(.appOnSurface)
                    .lineLimit(1)

                if isRefreshing {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.accent))
                        .scaleEffect(0.7)
                }
            }

            Spacer()

            Button(action: onNextWeek) {
                Image(systemName: "chevron.right")
                    .foregroundStyle(canNavigateNext
                        ? AnyShapeStyle(.appOnSurface)
                        : AnyShapeStyle(.appOnSurface.opacity(0.3)))
            }
            .disabled(!canNavigateNext)
        }
    }
}
