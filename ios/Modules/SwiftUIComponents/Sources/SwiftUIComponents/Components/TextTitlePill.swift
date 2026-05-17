import DesignSystem
import SwiftUI

public struct TextTitlePill: View {
    @Environment(\.appTheme) private var theme

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
                    .textStyle(titleStyle ?? theme.typography.titleSmall)
                    .lineLimit(1)
                    .foregroundStyle(.appOnSurface)
                Image(systemName: "chevron.right")
                    .font(theme.typography.labelSmall)
                    .foregroundStyle(.appOnSurface)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(.appOnSurface, lineWidth: 1)
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
    .appPreview()
}
