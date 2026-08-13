import SwiftUI

public struct AppPrimaryButtonStyle: ButtonStyle {
    @Environment(\.appTheme) private var theme
    @Environment(\.isEnabled) private var isEnabled
    @ScaledMetric(relativeTo: .footnote) private var horizontalPadding: CGFloat = 24
    @ScaledMetric(relativeTo: .footnote) private var verticalPadding: CGFloat = 12

    public init() {}

    public func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .textStyle(theme.typography.labelLarge)
            .foregroundStyle(.appOnButtonBackground)
            .padding(.horizontal, horizontalPadding)
            .padding(.vertical, verticalPadding)
            .background(
                .appButtonBackground,
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
