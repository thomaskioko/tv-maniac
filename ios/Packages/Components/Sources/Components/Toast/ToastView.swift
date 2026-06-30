import DesignSystem
import SwiftUI

public struct ToastView: View {
    @Environment(\.appTheme) private var theme

    private let type: ToastStyle
    private let title: String
    private let message: String
    private let loading: Bool
    private let onCancelTapped: () -> Void

    public init(
        type: ToastStyle,
        title: String,
        message: String,
        loading: Bool = false,
        onCancelTapped: @escaping () -> Void
    ) {
        self.type = type
        self.title = title
        self.message = message
        self.loading = loading
        self.onCancelTapped = onCancelTapped
    }

    public var body: some View {
        HStack(alignment: .center, spacing: theme.spacing.small) {
            if loading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.onPrimary))
                    .scaleEffect(0.8)
            } else {
                Button(action: onCancelTapped) {
                    Image(systemName: type.iconFileName)
                        .foregroundStyle(.appOnPrimary)
                        .textStyle(theme.typography.titleMedium)
                }
                .buttonStyle(.plain)
                .accessibilityLabel(Text("Dismiss"))
            }

            Text(message)
                .textStyle(theme.typography.bodyMedium)
                .foregroundStyle(.appOnPrimary)
                .lineLimit(3)
        }
        .padding(theme.spacing.medium)
        .frame(minWidth: 0, maxWidth: .infinity, alignment: .leading)
        .background(backgroundColor)
        .cornerRadius(theme.shapes.large)
        .padding(.horizontal, theme.spacing.medium)
    }

    private var backgroundColor: Color {
        switch type {
        case .error: .red
        case .warning: .orange
        case .info: .blue
        case .success: .green
        case .syncing: theme.colors.syncing
        }
    }
}

#Preview {
    VStack(spacing: 16) {
        ToastView(
            type: .error,
            title: "Error",
            message: "Something went wrong while syncing your data. Check your internet connection. If the problem persists, contact us.",
            onCancelTapped: {}
        )

        ToastView(
            type: .info,
            title: "Info",
            message: "Your data has been synced successfully.",
            onCancelTapped: {}
        )

        ToastView(
            type: .warning,
            title: "Warning",
            message: "Your session is about to expire.",
            onCancelTapped: {}
        )

        ToastView(
            type: .success,
            title: "Success",
            message: "Changes saved successfully.",
            onCancelTapped: {}
        )

        ToastView(
            type: .syncing,
            title: "Syncing",
            message: "Syncing your library",
            loading: true,
            onCancelTapped: {}
        )
    }
}
