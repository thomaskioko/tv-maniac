import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct DebugMenuView: View {
    @Theme private var theme

    private let presenter: DebugPresenter
    @StateObject @KotlinStateFlow private var uiState: DebugState

    init(presenter: DebugPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                notificationsRow
                    .padding(.top, theme.spacing.medium)

                Divider()
                    .overlay(theme.colors.onSurface.opacity(0.3))
                    .padding(.horizontal, theme.spacing.large)

                backgroundTasksRow

                Spacer()
                    .frame(height: theme.spacing.xLarge)
            }
            .padding(.horizontal, theme.spacing.medium)
            .padding(.top, DimensionConstants.toolbarInset)
        }
        .scrollContentBackground(.hidden)
        .background(theme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .swipeBackGesture {
            presenter.dispatch(action: BackClicked())
        }
        .overlay(
            GlassToolbar(
                title: String(\.label_debug_menu_title),
                opacity: 1.0,
                leadingIcon: {
                    GlassButton(icon: "chevron.left") {
                        presenter.dispatch(action: BackClicked())
                    }
                }
            ),
            alignment: .top
        )
        .edgesIgnoringSafeArea(.top)
    }

    @ViewBuilder
    private var notificationsRow: some View {
        Button {} label: {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("bell.fill", color: theme.colors.secondary)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(String(\.label_debug_section_notifications))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                    Text(String(\.label_debug_coming_soon))
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }
            .padding(.vertical, theme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private var backgroundTasksRow: some View {
        Button {} label: {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("arrow.triangle.2.circlepath", color: theme.colors.secondary)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(String(\.label_debug_section_background_tasks))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                    Text(String(\.label_debug_coming_soon))
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }
            .padding(.vertical, theme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private func settingsIcon(_ systemName: String, color: Color) -> some View {
        Image(systemName: systemName)
            .foregroundColor(color)
            .frame(width: theme.spacing.large, height: theme.spacing.large)
    }
}

private enum DimensionConstants {
    static var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }
}
