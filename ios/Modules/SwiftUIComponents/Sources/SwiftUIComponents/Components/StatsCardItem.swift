import DesignSystem
import SwiftUI

public struct StatsCardItem<Content: View>: View {
    @Environment(\.appTheme) private var theme

    private let systemImage: String
    private let title: String
    private let content: () -> Content

    public init(
        systemImage: String,
        title: String,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.systemImage = systemImage
        self.title = title
        self.content = content
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            iconBadge

            Spacer(minLength: theme.spacing.small)

            Text(title)
                .textStyle(theme.typography.bodySmall)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .lineLimit(1)
                .minimumScaleFactor(0.7)

            Spacer()
                .frame(height: theme.spacing.xxxSmall)

            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, theme.spacing.medium)
        .padding(.vertical, theme.spacing.small)
        .frame(height: 130)
        .background(theme.colors.surfaceVariant)
        .clipShape(RoundedRectangle(cornerRadius: theme.shapes.large))
        .appShadow(theme.shadows.small)
    }

    private var iconBadge: some View {
        Image(systemName: systemImage)
            .textStyle(theme.typography.titleMedium)
            .foregroundStyle(.appAccent)
            .frame(width: 36, height: 36)
            .background(theme.colors.accent.opacity(0.15))
            .clipShape(Circle())
    }
}

#Preview {
    let theme = LightTheme()
    ScrollView(.horizontal, showsIndicators: false) {
        HStack(alignment: .top, spacing: theme.spacing.small) {
            StatsCardItem(
                systemImage: "calendar",
                title: "Watch Time"
            ) {
                HStack(spacing: theme.spacing.large) {
                    VStack(spacing: theme.spacing.xxSmall) {
                        Text("14")
                            .textStyle(theme.typography.titleMedium)
                            .foregroundColor(theme.colors.onSurface)

                        Text("MONTHS")
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurface)
                    }
                    VStack(spacing: theme.spacing.xxSmall) {
                        Text("45")
                            .textStyle(theme.typography.titleMedium)
                            .foregroundColor(theme.colors.onSurface)

                        Text("DAYS")
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurface)
                    }
                    VStack(spacing: theme.spacing.xxSmall) {
                        Text("12")
                            .textStyle(theme.typography.titleMedium)
                            .foregroundColor(theme.colors.onSurface)

                        Text("HOURS")
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurface)
                    }
                }
            }

            StatsCardItem(
                systemImage: "tv.fill",
                title: "Episodes Watched"
            ) {
                VStack(spacing: 0) {
                    Text("5,123")
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                        .frame(maxWidth: .infinity)
                }.padding(theme.spacing.xSmall)
            }
        }
        .padding(.horizontal, theme.spacing.medium)
    }
}
