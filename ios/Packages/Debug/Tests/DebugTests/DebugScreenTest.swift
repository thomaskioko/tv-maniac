import Components
import Debug
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class DebugScreenTest: SnapshotTestCase {
    private let sampleItems: [DebugMenuItem] = [
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

    func test_DebugScreen() {
        DebugScreen(
            state: DebugScreen.State(
                title: "Debug Menu",
                items: sampleItems
            ),
            toast: .constant(nil),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DebugScreen")
    }

    func test_DebugScreen_WithLoadingItem() {
        let items: [DebugMenuItem] = [
            DebugMenuItem(
                id: "sync",
                icon: "arrow.triangle.2.circlepath",
                title: "Library Sync",
                subtitle: "Syncing...",
                isLoading: true,
                onTap: {}
            ),
            DebugMenuItem(
                id: "notification",
                icon: "bell.fill",
                title: "Episode Notifications",
                subtitle: "Send a test notification",
                onTap: {}
            ),
        ]

        DebugScreen(
            state: DebugScreen.State(
                title: "Debug Menu",
                items: items
            ),
            toast: .constant(nil),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DebugScreen_WithLoadingItem")
    }

    func test_DebugScreen_WithDisabledItem() {
        let items: [DebugMenuItem] = [
            DebugMenuItem(
                id: "sync",
                icon: "arrow.triangle.2.circlepath",
                title: "Library Sync",
                subtitle: "Not available",
                isEnabled: false,
                onTap: {}
            ),
            DebugMenuItem(
                id: "notification",
                icon: "bell.fill",
                title: "Episode Notifications",
                subtitle: "Send a test notification",
                onTap: {}
            ),
        ]

        DebugScreen(
            state: DebugScreen.State(
                title: "Debug Menu",
                items: items
            ),
            toast: .constant(nil),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DebugScreen_WithDisabledItem")
    }

    func test_DebugScreen_AccountType_Premium() {
        DebugScreen(
            state: DebugScreen.State(
                title: "Debug Menu",
                items: accountTypeItems(selectedId: "premium", subtitle: "Premium")
            ),
            toast: .constant(nil),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DebugScreen_AccountType_Premium")
    }

    func test_DebugScreen_AccountType_Free() {
        DebugScreen(
            state: DebugScreen.State(
                title: "Debug Menu",
                items: accountTypeItems(selectedId: "free", subtitle: "Free")
            ),
            toast: .constant(nil),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DebugScreen_AccountType_Free")
    }

    func test_DebugScreen_AccountType_Default() {
        DebugScreen(
            state: DebugScreen.State(
                title: "Debug Menu",
                items: accountTypeItems(selectedId: "none", subtitle: "Update account type")
            ),
            toast: .constant(nil),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DebugScreen_AccountType_Default")
    }

    private func accountTypeItems(selectedId: String, subtitle: String) -> [DebugMenuItem] {
        [
            DebugMenuItem(
                id: "account_type",
                icon: "person.fill",
                title: "Account Type",
                subtitle: subtitle,
                isEnabled: true,
                menuOptions: [
                    DebugMenuOption(id: "premium", label: "Premium", isSelected: selectedId == "premium", onSelect: {}),
                    DebugMenuOption(id: "free", label: "Free", isSelected: selectedId == "free", onSelect: {}),
                ],
                onTap: {}
            ),
            DebugMenuItem(
                id: "notification",
                icon: "bell.fill",
                title: "Episode Notifications",
                subtitle: "Send a test notification",
                onTap: {}
            ),
        ]
    }
}
