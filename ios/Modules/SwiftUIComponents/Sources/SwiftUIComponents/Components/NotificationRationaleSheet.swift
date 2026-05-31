import Components
import DesignSystem
import Models
import SwiftUI

public struct NotificationRationaleSheet: View {
    @Environment(\.appTheme) private var theme

    private let title: String
    private let message: String
    private let enableButtonText: String
    private let dismissButtonText: String
    private let onEnable: () -> Void
    private let onDismiss: () -> Void

    public init(
        title: String,
        message: String,
        enableButtonText: String,
        dismissButtonText: String,
        onEnable: @escaping () -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.title = title
        self.message = message
        self.enableButtonText = enableButtonText
        self.dismissButtonText = dismissButtonText
        self.onEnable = onEnable
        self.onDismiss = onDismiss
    }

    public var body: some View {
        VStack(spacing: theme.spacing.medium) {
            Spacer()
                .frame(height: theme.spacing.medium)

            Image(systemName: "bell.badge.fill")
                .symbolRenderingMode(.monochrome)
                .textStyle(theme.typography.displayMedium)
                .foregroundStyle(.appAccent)

            Text(title)
                .textStyle(theme.typography.headlineSmall)
                .foregroundStyle(.appOnSurface)

            Text(message)
                .textStyle(theme.typography.bodyMedium)
                .foregroundStyle(.appOnSurfaceVariant)
                .multilineTextAlignment(.center)
                .fixedSize(horizontal: false, vertical: true)
                .padding(.horizontal, theme.spacing.medium)

            episodeDateSection

            Spacer()
                .frame(height: theme.spacing.small)

            Button(action: onEnable) {
                Text(enableButtonText)
                    .textStyle(theme.typography.labelLarge)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.small))
            .controlSize(.large)
            .padding(.horizontal, theme.spacing.large)

            Button(action: onDismiss) {
                Text(dismissButtonText)
                    .textStyle(theme.typography.labelLarge)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderless)
            .controlSize(.large)
            .padding(.horizontal, theme.spacing.large)

            Spacer()
                .frame(height: theme.spacing.medium)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(.appSurface)
        .tint(theme.colors.accent)
    }

    private var episodeDateSection: some View {
        VStack(spacing: theme.spacing.xSmall) {
            gradientDivider

            HStack(spacing: theme.spacing.medium) {
                ForEach([12, 13, 14], id: \.self) { day in
                    Text("\(day)")
                        .textStyle(theme.typography.titleLarge)
                        .foregroundStyle(.appOnSurfaceVariant)
                }

                VStack(spacing: theme.spacing.xxxSmall) {
                    Text("15")
                        .textStyle(theme.typography.titleLarge)
                        .foregroundStyle(.appOnSurface)
                    Text("FEB")
                        .textStyle(theme.typography.labelSmall)
                        .foregroundStyle(.appOnSurface)
                }
                .frame(width: 56, height: 56)
                .overlay(
                    RoundedRectangle(cornerRadius: theme.shapes.medium)
                        .strokeBorder(.appAccent, lineWidth: 2)
                )

                ForEach([16, 17, 18], id: \.self) { day in
                    Text("\(day)")
                        .textStyle(theme.typography.titleLarge)
                        .foregroundStyle(.appOnSurfaceVariant)
                }
            }

            gradientDivider
        }
        .padding(.vertical, theme.spacing.xSmall)
    }

    private var gradientDivider: some View {
        Rectangle()
            .fill(
                LinearGradient(
                    colors: [.clear, theme.colors.onSurfaceVariant.opacity(0.3), .clear],
                    startPoint: .leading,
                    endPoint: .trailing
                )
            )
            .frame(height: 1)
            .padding(.horizontal, theme.spacing.large)
    }
}

#Preview {
    NotificationRationaleSheet(
        title: "Never miss new episodes",
        message: "Get notified when episodes from your followed shows are about to air so you never miss a premiere.",
        enableButtonText: "Enable Notifications",
        dismissButtonText: "Not Now",
        onEnable: {},
        onDismiss: {}
    )
}
