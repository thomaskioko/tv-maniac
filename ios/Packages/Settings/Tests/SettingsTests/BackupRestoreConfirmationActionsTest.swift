import Components
import DesignSystem
import Models
import Settings
import SnapshotTestingLib
import SwiftUI
import TvManiac
import XCTest

class BackupRestoreConfirmationActionsTest: SnapshotTestCase {
    func test_BackupRestoreConfirmationActions_Local() {
        VStack(alignment: .leading, spacing: 12) {
            BackupRestoreConfirmationActions(
                confirm: localConfirm,
                onConfirmAccount: {},
                onConfirmDevice: {},
                onCancel: {}
            )
        }
        .padding()
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "BackupRestoreConfirmationActions_Local")
    }

    func test_BackupRestoreConfirmationActions_Connected() {
        VStack(alignment: .leading, spacing: 12) {
            BackupRestoreConfirmationActions(
                confirm: connectedConfirm,
                onConfirmAccount: {},
                onConfirmDevice: {},
                onCancel: {}
            )
        }
        .padding()
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "BackupRestoreConfirmationActions_Connected")
    }

    private var localConfirm: BackupRestoreConfirmationDialogLocal {
        BackupRestoreConfirmationDialogLocal(
            title: "Restore this backup?",
            message: "This replaces the shows and watch history on this device. A copy of your current data is saved first.",
            cancelLabel: "Cancel",
            confirmLabel: "Restore"
        )
    }

    private var connectedConfirm: BackupRestoreConfirmationDialogConnected {
        BackupRestoreConfirmationDialogConnected(
            title: "Restore this backup?",
            message: "This replaces the shows and watch history on this device, and a copy of your current data is saved first. You are signed in to Trakt, so shows you do not add to it are removed at the next sync.",
            cancelLabel: "Cancel",
            accountLabel: "Add to Trakt",
            deviceLabel: "This device only"
        )
    }
}
