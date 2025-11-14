import SwiftUI

struct TvManiacButton: View {
    private let text: String
    private let color: Color
    private let textColor: Color
    private let borderColor: Color
    private let systemImageName: String?
    private let verticalPadding: CGFloat
    private let action: () -> Void

    init(
        text: String,
        color: Color = .accent,
        textColor: Color,
        borderColor: Color = .textColor,
        systemImageName: String?,
        verticalPadding: CGFloat = 16,
        action: @escaping () -> Void
    ) {
        self.text = text
        self.systemImageName = systemImageName
        self.color = color
        self.textColor = textColor
        self.borderColor = borderColor
        self.verticalPadding = verticalPadding
        self.action = action
    }

    var body: some View {
        Button(action: action) {
            HStack(spacing: 10) {
                if let image = systemImageName {
                    Image(systemName: image)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(height: 24)
                }

                Text(text)
                    .bodyMediumFont(size: 16)
                    .foregroundColor(textColor)
            }
            .foregroundColor(textColor)
            .padding(.vertical, verticalPadding)
            .padding(.horizontal, 20)
        }
    }
}
