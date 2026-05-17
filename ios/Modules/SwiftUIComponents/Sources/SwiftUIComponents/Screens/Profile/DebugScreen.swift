import DesignSystem
import SwiftUI

public struct DebugScreen: View {
    public struct State: Equatable {
        public let title: String
        public let items: [DebugMenuItem]

        public init(title: String, items: [DebugMenuItem]) {
            self.title = title
            self.items = items
        }
    }

    @Environment(\.appTheme) private var theme

    private let state: State
    @Binding private var toast: Toast?
    private let onBack: () -> Void

    public init(
        state: State,
        toast: Binding<Toast?>,
        onBack: @escaping () -> Void
    ) {
        self.state = state
        _toast = toast
        self.onBack = onBack
    }

    public var body: some View {
        List {
            Section {
                ForEach(Array(state.items.enumerated()), id: \.element.id) { index, item in
                    debugRow(for: item)
                        .listRowSeparator(.hidden, edges: index == 0 ? .top : [])
                }
            }
            .listRowBackground(theme.colors.background)
            .listRowSeparator(.visible)
            .listRowSeparatorTint(theme.colors.onSurface.opacity(0.8))
            .listRowInsets(EdgeInsets(top: 0, leading: theme.spacing.medium, bottom: 0, trailing: theme.spacing.medium))
            .alignmentGuide(.listRowSeparatorLeading) { d in
                d[.leading]
            }
            .alignmentGuide(.listRowSeparatorTrailing) { d in
                d[.trailing]
            }
        }
        .listStyle(.plain)
        .contentMargins(.top, toolbarInset + theme.spacing.medium)
        .scrollContentBackground(.hidden)
        .appScreen()
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .swipeBackGesture(onSwipe: onBack)
        .overlay(
            GlassToolbar(
                title: state.title,
                opacity: 1.0,
                leadingIcon: {
                    GlassButton(icon: "chevron.left", action: onBack)
                }
            ),
            alignment: .top
        )
        .edgesIgnoringSafeArea(.top)
        .toastView(toast: $toast)
    }

    private func debugRow(for item: DebugMenuItem) -> some View {
        HStack(spacing: theme.spacing.medium) {
            itemIcon(systemName: item.icon, role: item.role)

            VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                Text(item.title)
                    .textStyle(theme.typography.titleMedium)
                    .foregroundStyle(.appOnSurface)
                Text(item.subtitle)
                    .textStyle(theme.typography.bodySmall)
                    .foregroundStyle(.appOnSurfaceVariant)
            }

            Spacer()

            if item.isLoading {
                ProgressView()
                    .tint(theme.colors.secondary)
            } else {
                Image(systemName: "chevron.right")
                    .foregroundStyle(.appOnSurfaceVariant)
            }
        }
        .padding(.vertical, theme.spacing.small)
        .contentShape(Rectangle())
        .onTapGesture {
            guard item.isEnabled else { return }
            item.onTap()
        }
        .opacity(item.isEnabled ? 1.0 : 0.5)
    }

    private func itemIcon(systemName: String, role: DebugMenuItemRole) -> some View {
        Image(systemName: systemName)
            .foregroundStyle(role == .accent ? AnyShapeStyle(.appSecondary) : AnyShapeStyle(.appError))
            .frame(width: theme.spacing.large, height: theme.spacing.large)
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }
}

#Preview("Debug Screen") {
    DebugScreen(
        state: DebugScreen.State(
            title: "Debug Menu",
            items: [
                DebugMenuItem(
                    id: "notification",
                    icon: "bell.fill",
                    title: "Episode Notifications",
                    subtitle: "Send a test notification",
                    onTap: {}
                ),
                DebugMenuItem(
                    id: "delayed",
                    icon: "clock",
                    title: "Delayed Notification",
                    subtitle: "Schedule notification in 10 seconds",
                    onTap: {}
                ),
                DebugMenuItem(
                    id: "library-sync",
                    icon: "arrow.triangle.2.circlepath",
                    title: "Library Sync",
                    subtitle: "Last synced: Never",
                    onTap: {}
                ),
                DebugMenuItem(
                    id: "upnext-sync",
                    icon: "arrow.clockwise",
                    title: "Up Next Sync",
                    subtitle: "Last synced: 2 hours ago",
                    isLoading: false,
                    isEnabled: true,
                    onTap: {}
                ),
                DebugMenuItem(
                    id: "crash",
                    icon: "exclamationmark.triangle",
                    role: .destructive,
                    title: "Test Crash",
                    subtitle: "Trigger a fatal error",
                    onTap: {}
                ),
            ]
        ),
        toast: .constant(nil),
        onBack: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}
