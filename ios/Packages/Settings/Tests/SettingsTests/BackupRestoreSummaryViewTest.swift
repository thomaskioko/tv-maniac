import Components
import DesignSystem
import Models
import Settings
import SnapshotTestingLib
import SwiftUI
import XCTest

class BackupRestoreSummaryViewTest: SnapshotTestCase {
    func test_BackupRestoreSummaryView() {
        BackupRestoreSummaryView(content: restoreSummaryContent)
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "BackupRestoreSummaryView")
    }

    func test_BackupRestoreSummaryView_WithSkips() {
        BackupRestoreSummaryView(content: restoreSummaryContentWithSkips)
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "BackupRestoreSummaryView_WithSkips")
    }

    private var restoreSummaryContent: SettingsBackupSummaryContent {
        SettingsBackupSummaryContent(
            title: "Restore complete",
            showsRestored: "48 shows restored",
            episodesRestored: "612 episodes restored"
        )
    }

    private var restoreSummaryContentWithSkips: SettingsBackupSummaryContent {
        SettingsBackupSummaryContent(
            title: "Restore complete",
            showsRestored: "45 shows restored",
            episodesRestored: "598 episodes restored",
            showsSkipped: "3 shows couldn't be restored",
            skippedShows: ["Severance", "The Bear", "Shōgun"],
            rewatchNotice: "Rewatch history wasn't restored."
        )
    }
}
