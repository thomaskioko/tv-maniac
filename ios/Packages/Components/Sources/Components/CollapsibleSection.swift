import DesignSystem
import SwiftUI

public struct CollapsibleSection<Content: View>: View {
    @Environment(\.appTheme) private var theme
    @SwiftUI.State private var collapsed = false

    private let title: String
    private let showMore: Bool
    private let onMoreClick: () -> Void
    private let contentSpacing: CGFloat?
    private let content: () -> Content

    public init(
        title: String,
        showMore: Bool = false,
        onMoreClick: @escaping () -> Void = {},
        contentSpacing: CGFloat? = nil,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.title = title
        self.showMore = showMore
        self.onMoreClick = onMoreClick
        self.contentSpacing = contentSpacing
        self.content = content
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            header

            if !collapsed {
                Spacer().frame(height: contentSpacing ?? theme.spacing.small)
                content()
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .animation(.easeInOut(duration: 0.2), value: collapsed)
    }

    private var header: some View {
        HStack(alignment: .center, spacing: 0) {
            HStack(spacing: theme.spacing.xSmall) {
                Text(title)
                    .textStyle(theme.typography.titleLarge)
                    .foregroundStyle(.appOnSurface)
                    .lineLimit(1)

                if showMore, !collapsed {
                    Image(systemName: "chevron.right")
                        .textStyle(theme.typography.titleMedium)
                        .foregroundStyle(.appOnSurface)
                }
            }
            .contentShape(Rectangle())
            .onTapGesture {
                if showMore {
                    onMoreClick()
                }
            }

            Spacer(minLength: theme.spacing.medium)

            Image(systemName: "chevron.down")
                .textStyle(theme.typography.labelSmall)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .rotationEffect(.degrees(collapsed ? -180 : 0))
                .frame(width: DimensionConstants.toggleSize, height: DimensionConstants.toggleSize)
                .overlay(
                    Circle()
                        .stroke(theme.colors.onSurfaceVariant.opacity(0.5), lineWidth: 1)
                )
                .contentShape(Circle())
                .onTapGesture { collapsed.toggle() }
        }
        .padding(.horizontal, theme.spacing.medium)
    }
}

private enum DimensionConstants {
    static let toggleSize: CGFloat = 24
}

#Preview {
    CollapsibleSection(title: "Your Lists", showMore: true) {
        HStack(spacing: TvManiacSpacingScheme.default.small) {
            ForEach(0 ..< 2, id: \.self) { _ in
                RoundedRectangle(cornerRadius: TvManiacShapeScheme.default.large)
                    .fill(.appSurfaceVariant)
                    .frame(height: 120)
            }
        }
        .padding(.horizontal, TvManiacSpacingScheme.default.medium)
    }
}
