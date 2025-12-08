import SwiftUI

public struct OutlinedButton: View {
    @Theme private var theme

    private let text: String
    private let systemImageName: String?
    private let color: Color?
    private let textColor: Color?
    private let borderColor: Color?
    private let action: () -> Void

    public init(
        text: String,
        systemImageName: String? = nil,
        color: Color? = nil,
        textColor: Color? = nil,
        borderColor: Color? = nil,
        action: @escaping () -> Void
    ) {
        self.text = text
        self.systemImageName = systemImageName
        self.color = color
        self.textColor = textColor
        self.borderColor = borderColor
        self.action = action
    }

    public var body: some View {
        let resolvedColor = color ?? theme.colors.accent
        let resolvedBorderColor = borderColor ?? theme.colors.accent

        TvManiacButton(
            text: text,
            color: resolvedColor,
            textColor: resolvedColor,
            borderColor: resolvedBorderColor,
            systemImageName: systemImageName,
            action: action
        )
        .overlay(
            RoundedRectangle(cornerRadius: theme.shapes.small)
                .stroke(resolvedBorderColor, lineWidth: 2)
        )
    }
}

#Preview {
    OutlinedButton(
        text: "Watch Trailer",
        systemImageName: "film",
        action: {}
    )
}
