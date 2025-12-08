import SwiftUI

public struct RoundedButton: View {
    @Theme private var theme

    private let imageName: String
    private let tintColor: Color
    private let foregroundColor: Color?
    private let action: () -> Void

    public init(
        imageName: String,
        tintColor: Color,
        foregroundColor: Color? = nil,
        action: @escaping () -> Void
    ) {
        self.imageName = imageName
        self.tintColor = tintColor
        self.foregroundColor = foregroundColor
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            Image(systemName: imageName)
                .imageScale(.medium)
                .fontWeight(.semibold)
                .foregroundStyle(foregroundColor ?? theme.colors.onPrimary)
                .padding(.horizontal, theme.spacing.xxSmall)
                .padding(.vertical, theme.spacing.xxxSmall)
        }
        .buttonStyle(.borderedProminent)
        .contentShape(Circle())
        .clipShape(Circle())
        .tint(tintColor)
        .shadow(radius: 2.5)
    }
}
