import SwiftUI

public struct AppSecondaryButtonStyle: ButtonStyle {
    @Environment(\.appTheme) private var theme
    @Environment(\.isEnabled) private var isEnabled
    @ScaledMetric(relativeTo: .footnote) private var horizontalPadding: CGFloat = 24
    @ScaledMetric(relativeTo: .footnote) private var verticalPadding: CGFloat = 12

    public init() {}

    public func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .textStyle(theme.typography.labelLarge)
            .foregroundStyle(.appButtonBackground)
            .padding(.horizontal, horizontalPadding)
            .padding(.vertical, verticalPadding)
            .overlay(
                RoundedRectangle(cornerRadius: theme.shapes.medium)
                    .stroke(.appButtonBackground, lineWidth: 1)
            )
            .opacity(isEnabled ? (configuration.isPressed ? 0.8 : 1) : 0.5)
    }
}

public extension ButtonStyle where Self == AppSecondaryButtonStyle {
    static var appSecondary: AppSecondaryButtonStyle {
        .init()
    }
}
