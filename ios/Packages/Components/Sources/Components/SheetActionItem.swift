import DesignSystem
import SwiftUI

public struct SheetActionItem: View {
    @Environment(\.appTheme) private var theme

    private let icon: String
    private let label: String
    private let supportingText: String?
    private let isEnabled: Bool
    private let showProgress: Bool
    private let action: () -> Void

    public init(
        icon: String,
        label: String,
        supportingText: String? = nil,
        isEnabled: Bool = true,
        showProgress: Bool = false,
        action: @escaping () -> Void
    ) {
        self.icon = icon
        self.label = label
        self.supportingText = supportingText
        self.isEnabled = isEnabled
        self.showProgress = showProgress
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: theme.spacing.medium) {
                ZStack {
                    if showProgress {
                        ProgressView()
                            .controlSize(.small)
                            .tint(theme.colors.onSurfaceVariant)
                    } else {
                        Image(systemName: icon)
                            .textStyle(theme.typography.titleMedium)
                            .foregroundStyle(.appOnSurface)
                            .opacity(isEnabled ? 1 : 0.38)
                    }
                }
                .frame(width: 24)

                VStack(alignment: .leading, spacing: 0) {
                    Text(label)
                        .textStyle(theme.typography.bodyLarge)
                        .foregroundStyle(.appOnSurface)
                        .opacity(isEnabled ? 1 : 0.38)

                    if let supportingText {
                        Text(supportingText)
                            .textStyle(theme.typography.bodySmall)
                            .foregroundStyle(theme.colors.onSurfaceVariant)
                            .opacity(isEnabled ? 1 : 0.38)
                    }
                }

                Spacer()
            }
            .padding(.horizontal, theme.spacing.medium)
            .padding(.vertical, theme.spacing.small)
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!isEnabled)
    }
}

#Preview("Sheet action item") {
    VStack(spacing: 0) {
        SheetActionItem(icon: "checkmark.circle", label: "Just now", action: {})
        SheetActionItem(icon: "clock", label: "Release date", isEnabled: false, action: {})
        SheetActionItem(
            icon: "calendar",
            label: "Change watched date",
            supportingText: "12 Jan 2026 20:30",
            action: {}
        )
    }
}
