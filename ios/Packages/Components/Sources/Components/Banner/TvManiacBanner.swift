import DesignSystem
import SwiftUI

/// Persistent, dismissible banner pinned to the top of a screen. Pairs a message with an
/// optional action slot, styled via ``BannerStyle`` (error / warning / success / info).
///
/// Layout mirrors the Material 3 / GitHub Primer stacked-banner pattern: message + dismiss
/// share the top row, an optional action sits right-aligned on its own row below.
public struct TvManiacBanner<Action: View>: View {
    @Environment(\.appTheme) private var theme

    private let message: String
    private let style: BannerStyle
    private let dismissAccessibilityLabel: String
    private let onDismiss: () -> Void
    private let action: Action

    public init(
        message: String,
        style: BannerStyle = .info,
        dismissAccessibilityLabel: String,
        onDismiss: @escaping () -> Void,
        @ViewBuilder action: () -> Action
    ) {
        self.message = message
        self.style = style
        self.dismissAccessibilityLabel = dismissAccessibilityLabel
        self.onDismiss = onDismiss
        self.action = action()
    }

    public var body: some View {
        VStack(alignment: .trailing, spacing: theme.spacing.xSmall) {
            HStack(alignment: .top, spacing: theme.spacing.small) {
                Text(message)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(.white)
                    .lineLimit(4)
                    .frame(maxWidth: .infinity, alignment: .leading)

                Button(action: onDismiss) {
                    Image(systemName: "xmark")
                        .textStyle(theme.typography.labelLarge)
                        .foregroundStyle(.white)
                        .frame(width: 28, height: 28)
                        .contentShape(Rectangle())
                }
                .accessibilityLabel(dismissAccessibilityLabel)
            }

            action
        }
        .padding(.leading, theme.spacing.medium)
        .padding(.trailing, theme.spacing.xSmall)
        .padding(.vertical, theme.spacing.small)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(style.backgroundColor(theme: theme))
    }
}

public extension TvManiacBanner where Action == EmptyView {
    init(
        message: String,
        style: BannerStyle = .info,
        dismissAccessibilityLabel: String,
        onDismiss: @escaping () -> Void
    ) {
        self.init(
            message: message,
            style: style,
            dismissAccessibilityLabel: dismissAccessibilityLabel,
            onDismiss: onDismiss,
            action: { EmptyView() }
        )
    }
}

#Preview {
    VStack(spacing: TvManiacSpacingScheme.default.small) {
        TvManiacBanner(
            message: "Your Trakt account is full. Upgrade to keep syncing new shows.",
            style: .error,
            dismissAccessibilityLabel: "Dismiss",
            onDismiss: {}
        ) {
            Button("Upgrade", action: {})
                .foregroundStyle(.appError)
                .padding(.horizontal, TvManiacSpacingScheme.default.medium)
                .padding(.vertical, TvManiacSpacingScheme.default.xSmall)
                .background(Color.white, in: Capsule())
        }

        TvManiacBanner(
            message: "Your session is about to expire.",
            style: .warning,
            dismissAccessibilityLabel: "Dismiss",
            onDismiss: {}
        )

        TvManiacBanner(
            message: "Library synced successfully.",
            style: .success,
            dismissAccessibilityLabel: "Dismiss",
            onDismiss: {}
        )

        TvManiacBanner(
            message: "New episode notifications are now available.",
            style: .info,
            dismissAccessibilityLabel: "Dismiss",
            onDismiss: {}
        )
    }
}
