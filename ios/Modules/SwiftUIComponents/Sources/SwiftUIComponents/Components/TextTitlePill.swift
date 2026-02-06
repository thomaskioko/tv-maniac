import SwiftUI

public struct TextTitlePill: View {
    @Theme private var theme

    let title: String
    let titleStyle: Font?
    let onTap: () -> Void

    public init(
        title: String,
        titleStyle: Font? = nil,
        onTap: @escaping () -> Void
    ) {
        self.title = title
        self.titleStyle = titleStyle
        self.onTap = onTap
    }

    public var body: some View {
        Button(action: onTap) {
            HStack(spacing: 2) {
                Text(title)
                    .textStyle(titleStyle ?? theme.typography.titleMedium)
                    .lineLimit(1)
                    .foregroundColor(theme.colors.onSurface)
                Image(systemName: "chevron.right")
                    .font(.system(size: 10))
                    .foregroundColor(theme.colors.onSurface)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(theme.colors.onSurface, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    TextTitlePill(
        title: "The Walking Dead: Daryl Dixon",
        onTap: {}
    )
    .themedPreview()
}
