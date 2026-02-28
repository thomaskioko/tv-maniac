import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct DebugMenuView: View {
    private let presenter: DebugPresenter
    @StateObject @KotlinStateFlow private var uiState: DebugState
    @State private var toast: Toast?

    init(presenter: DebugPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        DebugScreen(
            title: String(\.label_debug_menu_title),
            items: menuItems,
            toast: $toast,
            onBack: { presenter.dispatch(action: BackClicked()) }
        )
        .onChange(of: uiState.message) { message in
            if let message {
                toast = Toast(type: .info, message: message.message)
                presenter.dispatch(action: DismissSnackbar(messageId: message.id))
            }
        }
    }

    private var menuItems: [DebugMenuItem] {
        [
            DebugMenuItem(
                id: "notification",
                icon: "bell.fill",
                title: String(\.label_settings_episode_notifications),
                subtitle: String(\.label_settings_debug_notification_description),
                isLoading: uiState.isSchedulingDebugNotification,
                isEnabled: !uiState.isSchedulingDebugNotification,
                onTap: { presenter.dispatch(action: TriggerDebugNotification()) }
            ),
            DebugMenuItem(
                id: "delayed-notification",
                icon: "clock",
                title: String(\.label_settings_delayed_debug_notification_title),
                subtitle: String(\.label_settings_delayed_debug_notification_description),
                isLoading: uiState.isSchedulingDebugNotification,
                isEnabled: !uiState.isSchedulingDebugNotification,
                onTap: { presenter.dispatch(action: TriggerDelayedDebugNotification()) }
            ),
            DebugMenuItem(
                id: "library-sync",
                icon: "arrow.triangle.2.circlepath",
                title: String(\.label_debug_library_sync_title),
                subtitle: syncSubtitle(for: uiState.lastLibrarySyncDate),
                isLoading: uiState.isSyncingLibrary,
                isEnabled: !uiState.isSyncingLibrary,
                onTap: { [self] in handleSyncTap { presenter.dispatch(action: TriggerLibrarySync()) } }
            ),
            DebugMenuItem(
                id: "upnext-sync",
                icon: "arrow.clockwise",
                title: String(\.label_debug_upnext_sync_title),
                subtitle: syncSubtitle(for: uiState.lastUpNextSyncDate),
                isLoading: uiState.isSyncingUpNext,
                isEnabled: !uiState.isSyncingUpNext,
                onTap: { [self] in handleSyncTap { presenter.dispatch(action: TriggerUpNextSync()) } }
            ),
            DebugMenuItem(
                id: "test-crash",
                icon: "exclamationmark.triangle",
                role: .destructive,
                title: String(\.label_debug_trigger_crash_title),
                subtitle: String(\.label_debug_trigger_crash_description),
                onTap: { fatalError("Test crash triggered from Debug Menu") }
            ),
        ]
    }

    private func handleSyncTap(action: @escaping () -> Void) {
        if uiState.isLoggedIn {
            action()
        } else {
            toast = Toast(type: .error, message: String(\.label_debug_sync_login_required))
        }
    }

    private func syncSubtitle(for date: String?) -> String {
        if let date {
            return String(\.label_settings_last_sync_date, parameter: date)
        }
        return String(\.label_debug_never_synced)
    }
}
