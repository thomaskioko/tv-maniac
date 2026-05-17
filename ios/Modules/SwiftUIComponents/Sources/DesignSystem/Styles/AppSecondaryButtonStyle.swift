import SwiftUI

public struct AppSecondaryButtonStyle: ButtonStyle {
    @Environment(\.appTheme) private var theme
    @Environment(\.isEnabled) private var isEnabled

    public init() {}

    public func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(theme.typography.labelLarge)
            .foregroundStyle(.appAccent)
            .padding(.horizontal, theme.spacing.large)
            .padding(.vertical, theme.spacing.small)
            .overlay(
                RoundedRectangle(cornerRadius: theme.shapes.medium)
                    .stroke(.appAccent, lineWidth: 1)
            )
            .opacity(isEnabled ? (configuration.isPressed ? 0.8 : 1) : 0.5)
    }
}

public extension ButtonStyle where Self == AppSecondaryButtonStyle {
    static var appSecondary: AppSecondaryButtonStyle {
        .init()
    }
}
