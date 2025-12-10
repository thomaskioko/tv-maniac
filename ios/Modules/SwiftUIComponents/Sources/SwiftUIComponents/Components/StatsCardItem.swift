import SwiftUI

public struct StatsCardItem<Content: View>: View {
    @Theme private var theme

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
            Spacer().frame(height: 18)

            HStack(spacing: theme.spacing.xSmall) {
                Spacer()

                Image(systemName: systemImage)
                    .textStyle(theme.typography.bodyLarge)
                    .foregroundColor(theme.colors.accent)

                Text(title)
                    .textStyle(theme.typography.titleMedium)
                    .foregroundColor(theme.colors.onSurface)

                Spacer()
            }
            .frame(maxWidth: .infinity)
            .padding(.top, theme.spacing.medium)

            Spacer().frame(height: theme.spacing.small)

            Rectangle()
                .fill(theme.colors.onSurface)
                .frame(height: 1)

            Spacer().frame(height: theme.spacing.xSmall)

            VStack {
                Spacer()

                content()
                    .frame(maxWidth: .infinity)

                Spacer()
            }
            .padding(.horizontal, theme.spacing.large)
            .padding(.bottom, theme.spacing.medium)
        }
        .frame(height: 120)
        .background(theme.colors.surface)
        .overlay(
            RoundedRectangle(cornerRadius: theme.shapes.small)
                .stroke(theme.colors.onSurface, lineWidth: 2)
        )
        .cornerRadius(theme.shapes.small)
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
