import DesignSystem
import SwiftUI

public struct AccountLimitBannerView: View {
    @Environment(\.appTheme) private var theme

    private let message: String
    private let upgradeTitle: String
    private let dismissAccessibilityLabel: String
    private let onUpgrade: () -> Void
    private let onDismiss: () -> Void

    public init(
        message: String,
        upgradeTitle: String,
        dismissAccessibilityLabel: String,
        onUpgrade: @escaping () -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.message = message
        self.upgradeTitle = upgradeTitle
        self.dismissAccessibilityLabel = dismissAccessibilityLabel
        self.onUpgrade = onUpgrade
        self.onDismiss = onDismiss
    }

    public var body: some View {
        TvManiacBanner(
            message: message,
            style: .error,
            dismissAccessibilityLabel: dismissAccessibilityLabel,
            onDismiss: onDismiss
        ) {
            Button(action: onUpgrade) {
                Text(upgradeTitle)
                    .textStyle(theme.typography.labelLarge)
                    .foregroundStyle(BannerStyle.error.backgroundColor)
                    .padding(.horizontal, theme.spacing.medium)
                    .padding(.vertical, theme.spacing.xSmall)
                    .background(Color.white, in: Capsule())
            }
            .accessibilityLabel(upgradeTitle)
        }
    }
}

#Preview {
    AccountLimitBannerView(
        message: "Your Trakt account is full. Upgrade to keep syncing new shows.",
        upgradeTitle: "Upgrade",
        dismissAccessibilityLabel: "Dismiss",
        onUpgrade: {},
        onDismiss: {}
    )
}
