import DesignSystem
import SwiftUI

public struct TextTitlePill: View {
    @Environment(\.appTheme) private var theme

    let title: String
    let titleStyle: TvManiacTextStyle?
    let onTap: () -> Void

    public init(
        title: String,
        titleStyle: TvManiacTextStyle? = nil,
        onTap: @escaping () -> Void
    ) {
        self.title = title
        self.titleStyle = titleStyle
        self.onTap = onTap
    }

    public var body: some View {
        Button(action: onTap) {
            HStack(spacing: theme.spacing.none) {
                Text(title)
                    .textStyle(titleStyle ?? theme.typography.titleSmall)
                    .lineLimit(1)
                    .foregroundStyle(.appOnSurface)
                Image(systemName: "chevron.right")
                    .textStyle(theme.typography.labelSmall)
                    .foregroundStyle(.appOnSurface)
            }
            .padding(.leading, theme.spacing.xSmall)
            .padding(.trailing, theme.spacing.xxSmall)
            .padding(.vertical, theme.spacing.xxSmall)
            .background(
                RoundedRectangle(cornerRadius: theme.shapes.large)
                    .fill(.appSurface)
            )
            .overlay(
                RoundedRectangle(cornerRadius: theme.shapes.large)
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
