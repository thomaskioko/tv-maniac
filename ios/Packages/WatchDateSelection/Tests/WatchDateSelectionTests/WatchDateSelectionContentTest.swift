import DesignSystem
import SnapshotTestingLib
import SwiftUI
import WatchDateSelection
import XCTest

class WatchDateSelectionContentTest: SnapshotTestCase {
    func test_WatchDateSelectionContent_Default() {
        makeSheet()
            .assertSnapshot(layout: .defaultDevice, testName: "WatchDateSelectionContent_Default")
    }

    func test_WatchDateSelectionContent_ReleaseDateDisabled() {
        makeSheet(isReleaseDateEnabled: false)
            .assertSnapshot(layout: .defaultDevice, testName: "WatchDateSelectionContent_ReleaseDateDisabled")
    }

    func test_WatchDateSelectionContent_EditWithDate() {
        makeSheet(title: "Change watched date", currentWatchedAtLabel: "12 Jan 2026 20:30")
            .assertSnapshot(layout: .defaultDevice, testName: "WatchDateSelectionContent_EditWithDate")
    }

    func test_WatchDateSelectionContent_EditWithUnknownDate() {
        makeSheet(title: "Change watched date", currentWatchedAtLabel: "A long time ago")
            .assertSnapshot(layout: .defaultDevice, testName: "WatchDateSelectionContent_EditWithUnknownDate")
    }

    private func makeSheet(
        title: String = "When did you watch this?",
        currentWatchedAtLabel: String? = nil,
        isReleaseDateEnabled: Bool = true
    ) -> some View {
        WatchDateSelectionContent(
            title: title,
            currentWatchedAtLabel: currentWatchedAtLabel,
            justNowLabel: "Just now",
            releaseDateLabel: "Release date",
            otherDateLabel: "Other date…",
            unknownDateLabel: "Unknown date",
            confirmLabel: "OK",
            cancelLabel: "Cancel",
            isReleaseDateEnabled: isReleaseDateEnabled,
            onJustNow: {},
            onReleaseDate: {},
            onOtherDate: { _ in },
            onUnknownDate: {}
        )
        .appPreview()
    }
}
