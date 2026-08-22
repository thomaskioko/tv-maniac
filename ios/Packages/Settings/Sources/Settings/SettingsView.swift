import Components
import DesignSystem
import Models
import SwiftUI
import TvManiac
import TvManiacKit
import UserNotifications

let tmdbURL = "https://www.themoviedb.org"
let traktURL = "https://trakt.tv"

public struct SettingsView: View {
    let presenter: SettingsPresenter
    @StateValue var uiState: SettingsState
    @StateObject var store = SettingsAppStorage.shared
    @State var showingLogoutAlert: Bool = false
    @State var showingSwitchAlert: Bool = false
    @State private var showingErrorAlert: Bool = false
    @State var showPolicy = false
    @State var showNotificationPermissionDeniedAlert = false
    @State private var showingBackupFolderPicker = false
    @State private var showingImportConfirm = false
    @State private var showingImportSource = false
    @Environment(\.openURL) var openURL
    @EnvironmentObject var appDelegate: AppDelegate

    public init(presenter: SettingsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    private var screenState: SettingsScreen<DeviceAppTheme>.State {
        SettingsScreen<DeviceAppTheme>.State(
            isLoading: uiState.isLoading,
            rootTitle: String(\.label_settings_title),
            currentPage: uiState.currentPage.toRoute(),
            rootSections: rootSections,
            themeItem: themeItem,
            imageQualityItem: imageQualityItem,
            layoutToggles: layoutToggles,
            fontSizeItem: fontSizeItem,
            discoverSectionsNavItem: discoverSectionsNavItem,
            discoverSectionToggles: discoverSectionToggles,
            posterStyleNavItem: posterStyleNavItem,
            posterStyleItem: posterStyleItem,
            behaviorToggles: behaviorToggles,
            notificationToggles: notificationToggles,
            privacyToggles: privacyToggles,
            privacyLinks: privacyLinks,
            infoContent: infoContent,
            licenseSections: licenseSections,
            accountContent: accountContent,
            backupContent: backupContent
        )
    }

    public var body: some View {
        TvManiacTypographyScheme.updateFontScale(percent: store.fontSizePercent)
        return SettingsScreen(
            state: screenState,
            onBack: { presenter.dispatch(action: BackClicked___()) }
        )
        .id(store.fontSizePercent)
        .settingsObservers(
            uiState: uiState,
            store: store,
            showingErrorAlert: $showingErrorAlert,
            showingSwitchAlert: $showingSwitchAlert
        )
        .settingsAlerts(
            uiState: uiState,
            alerts: SettingsAlertBindings(
                showingError: $showingErrorAlert,
                showingLogout: $showingLogoutAlert,
                showingSwitch: $showingSwitchAlert
            ),
            actions: SettingsAlertActions(
                onLogout: { presenter.dispatch(action: AccountLogoutClicked()) },
                onConfirmSwitch: { presenter.dispatch(action: ConfirmSwitchDiscard()) },
                onDismissSwitchDialog: { presenter.dispatch(action: DismissSwitchDialog()) },
                onDismissError: { id in presenter.dispatch(action: SettingsMessageShown(id: id)) }
            )
        )
        .alert(
            String(\.notification_permission_denied_title),
            isPresented: $showNotificationPermissionDeniedAlert
        ) {
            Button(String(\.notification_permission_denied_cancel), role: .cancel) {}
            Button(String(\.notification_permission_denied_settings)) {
                if let settingsUrl = URL(string: UIApplication.openSettingsURLString) {
                    openURL(settingsUrl)
                }
            }
        } message: {
            Text(String(\.notification_permission_denied_message))
        }
        .sheet(isPresented: $showPolicy) {
            if let url = URL(string: uiState.privacyPolicyUrl) {
                SFSafariViewWrapper(url: url)
                    .appTint()
                    .appTheme()
            }
        }
        .onAppear {
            store.imageQuality = uiState.imageQuality.toSwift()
            store.hapticFeedbackEnabled = uiState.hapticFeedbackEnabled
            store.fontSizePercent = Int(uiState.fontSizePercent)
            store.posterWidthScale = Double(uiState.posterWidth.scale)
            store.landscapeWidthScale = Double(uiState.landscapeWidth.scale)
            store.posterCornerRadius = Double(uiState.posterCornerStyle.cornerRadius)
        }
        .onChange(of: uiState.hapticFeedbackEnabled) { _, newValue in
            store.hapticFeedbackEnabled = newValue
        }
        .onChange(of: uiState.fontSizePercent) { _, newValue in
            store.fontSizePercent = Int(newValue)
        }
        .settingsPosterStyleObservers(uiState: uiState, store: store)
        .settingsBackupFolderPicker(
            uiState: uiState,
            presenter: presenter,
            isPresented: $showingBackupFolderPicker
        )
        .settingsBackupImporter(
            uiState: uiState,
            presenter: presenter,
            showingConfirm: $showingImportConfirm,
            showingSource: $showingImportSource
        )
        .screenTag(SettingsTestTags.shared.SCREEN_TEST_TAG)
    }
}
