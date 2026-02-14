import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class NotificationRationaleSheetTest: SnapshotTestCase {
    func test_NotificationRationaleSheet() {
        NotificationRationaleSheet(
            title: "Never miss new episodes",
            message: "Get notified when episodes from your followed shows are about to air so you never miss a premiere.",
            enableButtonText: "Enable Notifications",
            dismissButtonText: "Not Now",
            onEnable: {},
            onDismiss: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "NotificationRationaleSheet")
    }
}
