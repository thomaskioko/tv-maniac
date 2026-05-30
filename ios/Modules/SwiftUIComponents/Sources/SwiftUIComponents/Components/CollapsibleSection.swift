import DesignSystem
import SwiftUI

/// Profile section scaffold shared by every section (Stats, Your Lists, and the rails). Renders a
/// Trakt-style header — title, an optional trailing "more" chevron, and a collapse toggle — over
/// `content` that animates open and shut. Collapse state is per-session (in-memory).
public struct CollapsibleSection<Content: View>: View {
    @Environment(\.appTheme) private var theme
    @SwiftUI.State private var collapsed = false

    private let title: String
    private let showMore: Bool
    private let onMoreClick: () -> Void
    private let content: () -> Content

    public init(
        title: String,
        showMore: Bool = false,
        onMoreClick: @escaping () -> Void = {},
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.title = title
        self.showMore = showMore
        self.onMoreClick = onMoreClick
        self.content = content
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            header

            if !collapsed {
                Spacer().frame(height: theme.spacing.small)
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
                    .textStyle(theme.typography.titleLargeEmphasized)
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
                .textStyle(theme.typography.titleMedium)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .rotationEffect(.degrees(collapsed ? -180 : 0))
                .padding(theme.spacing.xxSmall)
                .contentShape(Rectangle())
                .onTapGesture { collapsed.toggle() }
        }
        .padding(.horizontal, theme.spacing.medium)
    }
}

#Preview {
    CollapsibleSection(title: "Your Lists", showMore: true, onMoreClick: {}) {
        HStack(spacing: 12) {
            ForEach(0 ..< 2, id: \.self) { _ in
                RoundedRectangle(cornerRadius: 16)
                    .fill(Color.gray.opacity(0.3))
                    .frame(height: 120)
            }
        }
        .padding(.horizontal, 16)
    }
}
