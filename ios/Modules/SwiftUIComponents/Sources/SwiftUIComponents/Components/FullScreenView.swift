import SwiftUI

public struct EmptyStateView: View {
    @Theme private var theme

    private let systemName: String
    private let title: String
    private let message: String?
    private let buttonText: String?
    private let action: () -> Void

    public init(
        systemName: String = "tray",
        title: String,
        message: String? = nil,
        buttonText: String? = nil,
        action: @escaping () -> Void = {}
    ) {
        self.systemName = systemName
        self.title = title
        self.message = message
        self.buttonText = buttonText
        self.action = action
    }

    public var body: some View {
        VStack(spacing: 0) {
            Image(systemName: systemName)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .foregroundColor(theme.colors.onSurfaceVariant)
                .frame(width: 64, height: 64)

            Spacer().frame(height: theme.spacing.large)

            Text(title)
                .textStyle(theme.typography.titleMedium)
                .foregroundColor(theme.colors.onSurface)
                .multilineTextAlignment(.center)

            if let message {
                Spacer().frame(height: theme.spacing.xSmall)

                Text(message)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundColor(theme.colors.onSurfaceVariant)
                    .multilineTextAlignment(.center)
            }

            if let buttonText {
                Spacer().frame(height: theme.spacing.large)

                Button(action: action) {
                    Text(buttonText)
                        .textStyle(theme.typography.labelMedium)
                        .foregroundColor(theme.colors.accent)
                        .padding(.vertical, theme.spacing.xSmall)
                        .padding(.horizontal, theme.spacing.large)
                }
                .overlay(
                    RoundedRectangle(cornerRadius: theme.shapes.small)
                        .stroke(theme.colors.accent, lineWidth: 1.5)
                )
            }
        }
        .padding(.horizontal, theme.spacing.medium)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview {
    EmptyStateView(
        title: "Nothing here yet",
        message: "Shows you follow will appear here."
    )
}

#Preview {
    EmptyStateView(
        systemName: "exclamationmark.triangle",
        title: "Something went wrong",
        message: "We couldn't load the data.",
        buttonText: "Retry"
    )
}
