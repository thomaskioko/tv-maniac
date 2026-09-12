import DesignSystem
import SnapshotTestingLib
import SwiftUI
import WatchDateSelection
import XCTest

class WatchDateSelectionContentTest: SnapshotTestCase {
    func test_WatchDateSelectionContent_Default() {
        let view = makeSheet()
        view.assertSnapshot(layout: .defaultDevice, testName: "WatchDateSelectionContent_Default")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "WatchDateSelectionContent_Default")
    }

    func test_WatchDateSelectionContent_ReleaseDateDisabled() {
        let view = makeSheet(isReleaseDateEnabled: false)
        view.assertSnapshot(layout: .defaultDevice, testName: "WatchDateSelectionContent_ReleaseDateDisabled")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "WatchDateSelectionContent_ReleaseDateDisabled")
    }

    func test_WatchDateSelectionContent_EditWithDate() {
        let view = makeSheet(title: "Change watched date", currentWatchedAtLabel: "12 Jan 2026 20:30")
        view.assertSnapshot(layout: .defaultDevice, testName: "WatchDateSelectionContent_EditWithDate")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "WatchDateSelectionContent_EditWithDate")
    }

    func test_WatchDateSelectionContent_EditWithUnknownDate() {
        let view = makeSheet(title: "Change watched date", currentWatchedAtLabel: "A long time ago")
        view.assertSnapshot(layout: .defaultDevice, testName: "WatchDateSelectionContent_EditWithUnknownDate")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "WatchDateSelectionContent_EditWithUnknownDate")
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
