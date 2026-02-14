import SwiftUI

public struct ToastView: View {
    @Theme private var theme

    private let type: ToastStyle
    private let title: String
    private let message: String
    private let onCancelTapped: () -> Void

    public init(
        type: ToastStyle,
        title: String,
        message: String,
        onCancelTapped: @escaping () -> Void
    ) {
        self.type = type
        self.title = title
        self.message = message
        self.onCancelTapped = onCancelTapped
    }

    public var body: some View {
        HStack(alignment: .center, spacing: theme.spacing.small) {
            Image(systemName: type.iconFileName)
                .foregroundColor(.white)
                .font(.title3)

            Text(message)
                .textStyle(theme.typography.bodyMedium)
                .foregroundColor(.white)
                .lineLimit(3)
        }
        .padding(theme.spacing.medium)
        .frame(minWidth: 0, maxWidth: .infinity, alignment: .leading)
        .background(type.themeColor)
        .cornerRadius(theme.shapes.large)
        .padding(.horizontal, theme.spacing.medium)
        .onTapGesture {
            onCancelTapped()
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
    }
}
