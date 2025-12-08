import SwiftUI

struct TvManiacButton: View {
    @Theme private var theme
    private let text: String
    private let color: Color?
    private let textColor: Color?
    private let borderColor: Color?
    private let systemImageName: String?
    private let verticalPadding: CGFloat?
    private let action: () -> Void

    init(
        text: String,
        color: Color? = nil,
        textColor: Color? = nil,
        borderColor: Color? = nil,
        systemImageName: String?,
        verticalPadding: CGFloat? = nil,
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
            HStack(spacing: theme.spacing.small) {
                if let image = systemImageName {
                    Image(systemName: image)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(height: theme.spacing.large)
                }

                Text(text)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundColor(textColor ?? theme.colors.onSurface)
            }
            .foregroundColor(textColor ?? theme.colors.onSurface)
            .padding(.vertical, verticalPadding ?? theme.spacing.medium)
            .padding(.horizontal, theme.spacing.large)
        }
    }
}
