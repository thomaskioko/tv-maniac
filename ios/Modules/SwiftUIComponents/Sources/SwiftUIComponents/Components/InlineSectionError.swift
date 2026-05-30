import DesignSystem
import SwiftUI

/// Compact, non-intrusive error row for a section that fails to load. Trakt-style: a single muted
/// line with an optional inline retry link, so a failed rail does not dominate a screen that stacks
/// several sections.
public struct InlineSectionError: View {
    @Environment(\.appTheme) private var theme

    private let message: String
    private let retryLabel: String?
    private let onRetry: (() -> Void)?

    public init(
        message: String,
        retryLabel: String? = nil,
        onRetry: (() -> Void)? = nil
    ) {
        self.message = message
        self.retryLabel = retryLabel
        self.onRetry = onRetry
    }

    public var body: some View {
        HStack(alignment: .center, spacing: theme.spacing.medium) {
            Text(message)
                .textStyle(theme.typography.bodySmall)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .lineLimit(2)
                .frame(maxWidth: .infinity, alignment: .leading)

            if let retryLabel, let onRetry {
                Button(action: onRetry) {
                    Text(retryLabel)
                        .textStyle(theme.typography.labelLarge)
                        .foregroundStyle(theme.colors.secondary)
                }
            }
        }
        .padding(.horizontal, theme.spacing.medium)
        .padding(.vertical, theme.spacing.small)
    }
}

#Preview {
    InlineSectionError(message: "Couldn't load your lists.", retryLabel: "Retry", onRetry: {})
}
