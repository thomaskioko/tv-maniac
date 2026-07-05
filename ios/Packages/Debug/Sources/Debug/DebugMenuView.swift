import Components
import DesignSystem
import SwiftUI
import TvManiac
import TvManiacKit

public struct DebugMenuView: View {
    private let presenter: DebugPresenter
    @StateValue private var uiState: DebugState
    @State private var toast: Toast?

    public init(presenter: DebugPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    public var body: some View {
        DebugScreen(
            state: DebugScreen.State(
                title: uiState.title,
                items: uiState.items.map { $0.toMenuItem(presenter: presenter, accountType: uiState.accountType) }
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

private let accountTypeItemId = "account_type"

private extension DebugItem {
    func toMenuItem(presenter: DebugPresenter, accountType: AccountType) -> DebugMenuItem {
        if id == accountTypeItemId {
            return DebugMenuItem(
                id: id,
                icon: icon.toSymbolName(),
                role: role.toMenuItemRole(),
                title: title,
                subtitle: subtitle,
                isLoading: isLoading,
                isEnabled: true,
                menuOptions: AccountType.entries
                    .filter { $0 != AccountType.none }
                    .map { type in
                        DebugMenuOption(
                            id: type.name,
                            label: type.label,
                            isSelected: type == accountType,
                            onSelect: {
                                presenter.dispatch(action: SetAccountType(accountType: type))
                            }
                        )
                    },
                onTap: {}
            )
        }

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
        case "Account": "person.fill"
        case "Warning": "exclamationmark.triangle"
        default: "questionmark.circle"
        }
    }
}

private extension AccountType {
    var label: String {
        if self == AccountType.premium { return String(\.label_debug_account_type_premium) }
        return String(\.label_debug_account_type_free)
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
