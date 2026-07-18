import DesignSystem
import SwiftUI

public struct FilledImageButton: View {
    @Environment(\.appTheme) private var theme

    private let text: String
    private let systemImageName: String?
    private let color: Color?
    private let textColor: Color?
    private let borderColor: Color?
    private let cornerRadius: CGFloat?
    private let verticalPadding: CGFloat?
    private let action: () -> Void

    public init(
        text: String,
        systemImageName: String? = nil,
        color: Color? = nil,
        textColor: Color? = nil,
        borderColor: Color? = nil,
        cornerRadius: CGFloat? = nil,
        verticalPadding: CGFloat? = nil,
        action: @escaping () -> Void
    ) {
        self.text = text
        self.systemImageName = systemImageName
        self.color = color
        self.textColor = textColor
        self.borderColor = borderColor
        self.cornerRadius = cornerRadius
        self.verticalPadding = verticalPadding
        self.action = action
    }

    public var body: some View {
        let resolvedColor = color ?? theme.colors.buttonBackground
        let resolvedBorderColor = borderColor ?? theme.colors.buttonBackground
        let resolvedCornerRadius = cornerRadius ?? theme.shapes.small

        TvManiacButton(
            text: text,
            color: resolvedColor,
            textColor: theme.colors.onButtonBackground,
            borderColor: resolvedBorderColor,
            systemImageName: systemImageName,
            verticalPadding: verticalPadding ?? theme.spacing.medium,
            action: action
        )
        .background(
            RoundedRectangle(cornerRadius: resolvedCornerRadius)
                .foregroundColor(resolvedColor)
        )
    }
}

#Preview {
    FilledImageButton(
        text: "Watch Trailer",
        systemImageName: "film",
        action: {}
    )
}
