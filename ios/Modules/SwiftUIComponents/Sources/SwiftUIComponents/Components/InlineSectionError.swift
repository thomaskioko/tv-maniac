import DesignSystem
import SwiftUI

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
        VStack(alignment: .center, spacing: theme.spacing.xxSmall) {
            Text(message)
                .textStyle(theme.typography.bodySmall)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .multilineTextAlignment(.center)
                .lineLimit(2)

            if let retryLabel, let onRetry {
                Button(action: onRetry) {
                    Text(retryLabel)
                        .textStyle(theme.typography.labelLarge)
                        .foregroundStyle(theme.colors.secondary)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .center)
        .padding(.horizontal, theme.spacing.medium)
    }
}

#Preview {
    InlineSectionError(message: "Couldn't load your lists.", retryLabel: "Retry", onRetry: {})
}
