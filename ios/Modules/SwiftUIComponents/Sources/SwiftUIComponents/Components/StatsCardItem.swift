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
        VStack(spacing: 0) {
            HStack(spacing: theme.spacing.xSmall) {
                Image(systemName: systemImage)
                    .textStyle(theme.typography.bodyLarge)
                    .foregroundStyle(.appAccent)

                Text(title)
                    .textStyle(theme.typography.titleSmall)
                    .foregroundStyle(.appOnSurface)
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)

                Spacer()
            }
            .frame(maxWidth: .infinity)
            .padding(.horizontal, theme.spacing.medium)
            .padding(.vertical, theme.spacing.medium)

            Rectangle()
                .fill(theme.colors.onSurfaceVariant)
                .frame(height: 1)

            VStack {
                Spacer()

                content()
                    .frame(maxWidth: .infinity)

                Spacer()
            }
            .padding(.horizontal, theme.spacing.large)
        }
        .frame(height: 120)
        .background(theme.colors.surface)
        .overlay(
            Rectangle()
                .stroke(theme.colors.outline.opacity(0.2), lineWidth: 0.5)
        )
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
                systemImage: "tv",
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
