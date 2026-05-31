import Components
import DesignSystem
import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct DebugMenuView: View {
    private let presenter: DebugPresenter
    @StateValue private var uiState: DebugState
    @State private var toast: Toast?

    init(presenter: DebugPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    var body: some View {
        DebugScreen(
            state: DebugScreen.State(
                title: uiState.title,
                items: uiState.items.map { $0.toMenuItem(presenter: presenter) }
            ),
            toast: $toast,
            onBack: { presenter.dispatch(action: BackClicked()) }
        )
        .onChange(of: uiState.message) { _, message in
            if let message {
                toast = Toast(type: .info, message: message.message)
                presenter.dispatch(action: DismissSnackbar(messageId: message.id))
            }
        }
    }
}

private extension DebugItem {
    func toMenuItem(presenter: DebugPresenter) -> DebugMenuItem {
        let action = action
        return DebugMenuItem(
            id: id,
            icon: icon.toSymbolName(),
            role: role.toMenuItemRole(),
            title: title,
            subtitle: subtitle,
            isLoading: isLoading,
            isEnabled: action != nil && !isLoading,
            onTap: {
                if let action {
                    presenter.dispatch(action: action)
                }
            }
        )
    }
}

private extension DebugItemIcon {
    func toSymbolName() -> String {
        switch name {
        case "Notifications": "bell.fill"
        case "Schedule": "clock"
        case "LibrarySync": "arrow.triangle.2.circlepath"
        case "UpNextSync": "arrow.clockwise"
        case "FeatureFlags": "flag"
        case "Key": "key.fill"
        case "Warning": "exclamationmark.triangle"
        default: "questionmark.circle"
        }
    }
}

private extension DebugItemRole {
    func toMenuItemRole() -> DebugMenuItemRole {
        switch name {
        case "Destructive": .destructive
        default: .accent
        }
    }
}
