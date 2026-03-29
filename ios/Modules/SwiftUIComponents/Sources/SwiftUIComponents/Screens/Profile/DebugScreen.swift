import SwiftUI

public struct DebugScreen: View {
    @Theme private var theme

    private let title: String
    private let items: [DebugMenuItem]
    @Binding private var toast: Toast?
    private let onBack: () -> Void

    public init(
        title: String,
        items: [DebugMenuItem],
        toast: Binding<Toast?>,
        onBack: @escaping () -> Void
    ) {
        self.title = title
        self.items = items
        _toast = toast
        self.onBack = onBack
    }

    public var body: some View {
        List {
            Section {
                ForEach(Array(items.enumerated()), id: \.element.id) { index, item in
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
        .background(theme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .swipeBackGesture(onSwipe: onBack)
        .overlay(
            GlassToolbar(
                title: title,
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
                    .foregroundColor(theme.colors.onSurface)
                Text(item.subtitle)
                    .textStyle(theme.typography.bodySmall)
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }

            Spacer()

            if item.isLoading {
                ProgressView()
                    .tint(theme.colors.secondary)
            } else {
                Image(systemName: "chevron.right")
                    .foregroundColor(theme.colors.onSurfaceVariant)
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
            .foregroundColor(role == .accent ? theme.colors.secondary : theme.colors.error)
            .frame(width: theme.spacing.large, height: theme.spacing.large)
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }
}

#Preview("Debug Screen") {
    ThemedPreview {
        DebugScreen(
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
            ],
            toast: .constant(nil),
            onBack: {}
        )
    }
    .preferredColorScheme(.dark)
}
