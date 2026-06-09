import SwiftUI

public struct AppPrimaryButtonStyle: ButtonStyle {
    @Environment(\.appTheme) private var theme
    @Environment(\.isEnabled) private var isEnabled

    public init() {}

    public func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .textStyle(theme.typography.labelLarge)
            .foregroundStyle(.appOnPrimary)
            .padding(.horizontal, theme.spacing.large)
            .padding(.vertical, theme.spacing.small)
            .background(
                .appPrimary,
                in: RoundedRectangle(cornerRadius: theme.shapes.medium)
            )
            .opacity(isEnabled ? (configuration.isPressed ? 0.8 : 1) : 0.5)
    }
}

public extension ButtonStyle where Self == AppPrimaryButtonStyle {
    static var appPrimary: AppPrimaryButtonStyle {
        .init()
    }
}
