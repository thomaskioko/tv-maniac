import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct DebugMenuView: View {
    @Theme private var theme

    private let presenter: DebugPresenter
    @StateObject @KotlinStateFlow private var uiState: DebugState
    @State private var toast: Toast?

    init(presenter: DebugPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                debugNotificationRow
                    .padding(.top, theme.spacing.medium)

                Divider()
                    .overlay(theme.colors.onSurface.opacity(0.12))
                    .padding(.horizontal, theme.spacing.large)

                delayedDebugNotificationRow

                Divider()
                    .overlay(theme.colors.onSurface.opacity(0.12))
                    .padding(.horizontal, theme.spacing.large)

                librarySyncRow

                Divider()
                    .overlay(theme.colors.onSurface.opacity(0.12))
                    .padding(.horizontal, theme.spacing.large)

                upNextSyncRow

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
        .onChange(of: uiState.message) { message in
            if let message {
                toast = Toast(
                    type: .info,
                    message: message.message
                )
                presenter.dispatch(action: DismissSnackbar(messageId: message.id))
            }
        }
        .toastView(toast: $toast)
    }

    @ViewBuilder
    private var librarySyncRow: some View {
        Button {
            if uiState.isLoggedIn {
                presenter.dispatch(action: TriggerLibrarySync())
            } else {
                toast = Toast(
                    type: .error,
                    message: String(\.label_debug_sync_login_required)
                )
            }
        } label: {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("arrow.triangle.2.circlepath", color: theme.colors.secondary)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(String(\.label_debug_library_sync_title))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                    Text(syncSubtitle(for: uiState.lastLibrarySyncDate))
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }

                Spacer()

                if uiState.isSyncingLibrary {
                    ProgressView()
                        .tint(theme.colors.secondary)
                } else {
                    Image(systemName: "chevron.right")
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }
            }
            .padding(.vertical, theme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(uiState.isSyncingLibrary)
    }

    @ViewBuilder
    private var upNextSyncRow: some View {
        Button {
            if uiState.isLoggedIn {
                presenter.dispatch(action: TriggerUpNextSync())
            } else {
                toast = Toast(
                    type: .error,
                    message: String(\.label_debug_sync_login_required)
                )
            }
        } label: {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("arrow.clockwise", color: theme.colors.secondary)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(String(\.label_debug_upnext_sync_title))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                    Text(syncSubtitle(for: uiState.lastUpNextSyncDate))
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }

                Spacer()

                if uiState.isSyncingUpNext {
                    ProgressView()
                        .tint(theme.colors.secondary)
                } else {
                    Image(systemName: "chevron.right")
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }
            }
            .padding(.vertical, theme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(uiState.isSyncingUpNext)
    }

    @ViewBuilder
    private var debugNotificationRow: some View {
        Button {
            presenter.dispatch(action: TriggerDebugNotification())
        } label: {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("bell.fill", color: theme.colors.secondary)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(String(\.label_settings_episode_notifications))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                    Text(String(\.label_settings_debug_notification_description))
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }

                Spacer()

                if uiState.isSchedulingDebugNotification {
                    ProgressView()
                        .tint(theme.colors.secondary)
                } else {
                    Image(systemName: "chevron.right")
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }
            }
            .padding(.vertical, theme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(uiState.isSchedulingDebugNotification)
    }

    @ViewBuilder
    private var delayedDebugNotificationRow: some View {
        Button {
            presenter.dispatch(action: TriggerDelayedDebugNotification())
            toast = Toast(
                type: .info,
                message: String(\.label_settings_debug_notification_scheduled)
            )
        } label: {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("clock", color: theme.colors.secondary)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(String(\.label_settings_delayed_debug_notification_title))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                    Text(String(\.label_settings_delayed_debug_notification_description))
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }

                Spacer()

                if uiState.isSchedulingDebugNotification {
                    ProgressView()
                        .tint(theme.colors.secondary)
                } else {
                    Image(systemName: "chevron.right")
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }
            }
            .padding(.vertical, theme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(uiState.isSchedulingDebugNotification)
    }

    @ViewBuilder
    private func settingsIcon(_ systemName: String, color: Color) -> some View {
        Image(systemName: systemName)
            .foregroundColor(color)
            .frame(width: theme.spacing.large, height: theme.spacing.large)
    }

    private func syncSubtitle(for date: String?) -> String {
        if let date {
            return String(\.label_settings_last_sync_date, parameter: date)
        }
        return String(\.label_debug_never_synced)
    }
}

private enum DimensionConstants {
    static var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }
}
