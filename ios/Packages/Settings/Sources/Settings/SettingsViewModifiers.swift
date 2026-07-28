import Components
import DesignSystem
import Models
import SwiftUI
import TvManiac
import TvManiacKit
import UserNotifications

extension View {
    func settingsObservers(
        uiState: SettingsState,
        store: SettingsAppStorage,
        showingErrorAlert: Binding<Bool>,
        showingSwitchAlert: Binding<Bool>
    ) -> some View {
        onChange(of: uiState.theme) { _, newTheme in
            store.appTheme = newTheme.toDeviceAppTheme()
        }
        .onChange(of: uiState.imageQuality) { _, imageQuality in
            store.imageQuality = imageQuality.toSwift()
        }
        .onChange(of: uiState.message) { _, message in
            showingErrorAlert.wrappedValue = message != nil
        }
        .onChange(of: uiState.showSwitchConfirmation) { _, show in
            showingSwitchAlert.wrappedValue = show
        }
    }

    func settingsPosterStyleObservers(uiState: SettingsState, store: SettingsAppStorage) -> some View {
        onChange(of: uiState.posterWidth) { _, newValue in
            store.posterWidthScale = Double(newValue.scale)
        }
        .onChange(of: uiState.landscapeWidth) { _, newValue in
            store.landscapeWidthScale = Double(newValue.scale)
        }
        .onChange(of: uiState.posterCornerStyle) { _, newValue in
            store.posterCornerRadius = Double(newValue.cornerRadius)
        }
    }

    func settingsAlerts(
        uiState: SettingsState,
        alerts: SettingsAlertBindings,
        actions: SettingsAlertActions
    ) -> some View {
        alert(isPresented: alerts.showingError) {
            Alert(
                title: Text(String(\.label_error)),
                message: Text(uiState.message?.message ?? String(\.error_generic)),
                dismissButton: .default(Text(String(\.label_ok))) {
                    if let message = uiState.message {
                        actions.onDismissError(message.id)
                    }
                }
            )
        }
        .alert(isPresented: alerts.showingLogout) {
            Alert(
                title: Text(String(\.trakt_dialog_logout_title)),
                message: Text(String(\.trakt_dialog_logout_message)),
                primaryButton: .destructive(Text(String(\.logout))) {
                    actions.onLogout()
                },
                secondaryButton: .cancel()
            )
        }
        .alert(
            uiState.switchDialogTitle ?? "",
            isPresented: alerts.showingSwitch,
            actions: {
                Button(uiState.labels.switchConfirm, role: .destructive) {
                    actions.onConfirmSwitch()
                }
                Button(uiState.labels.switchCancel, role: .cancel) {
                    actions.onDismissSwitchDialog()
                }
            },
            message: {
                if let message = uiState.switchDialogMessage {
                    Text(message)
                }
            }
        )
    }
}

struct SettingsAlertBindings {
    let showingError: Binding<Bool>
    let showingLogout: Binding<Bool>
    let showingSwitch: Binding<Bool>
}

struct SettingsAlertActions {
    let onLogout: () -> Void
    let onConfirmSwitch: () -> Void
    let onDismissSwitchDialog: () -> Void
    let onDismissError: (Int64) -> Void
}
