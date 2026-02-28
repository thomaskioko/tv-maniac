import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
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
            title: "Debug Menu",
            items: sampleItems,
            toast: .constant(nil),
            onBack: {}
        )
        .themedPreview()
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
            title: "Debug Menu",
            items: items,
            toast: .constant(nil),
            onBack: {}
        )
        .themedPreview()
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
            title: "Debug Menu",
            items: items,
            toast: .constant(nil),
            onBack: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DebugScreen_WithDisabledItem")
    }
}
