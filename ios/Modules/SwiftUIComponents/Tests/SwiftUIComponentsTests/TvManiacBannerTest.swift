import DesignSystem
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class TvManiacBannerTest: SnapshotTestCase {
    func test_TvManiacBanner_Error() {
        TvManiacBanner(
            message: "Your Trakt account is full. Upgrade to keep syncing new shows.",
            style: .error,
            dismissAccessibilityLabel: "Dismiss",
            onDismiss: {}
        )
        .appPreview()
        .assertSnapshot(testName: "TvManiacBanner_Error")
    }

    func test_TvManiacBanner_Warning() {
        TvManiacBanner(
            message: "Your session is about to expire.",
            style: .warning,
            dismissAccessibilityLabel: "Dismiss",
            onDismiss: {}
        )
        .appPreview()
        .assertSnapshot(testName: "TvManiacBanner_Warning")
    }

    func test_TvManiacBanner_Success() {
        TvManiacBanner(
            message: "Library synced successfully.",
            style: .success,
            dismissAccessibilityLabel: "Dismiss",
            onDismiss: {}
        )
        .appPreview()
        .assertSnapshot(testName: "TvManiacBanner_Success")
    }

    func test_TvManiacBanner_Info() {
        TvManiacBanner(
            message: "New episode notifications are now available.",
            style: .info,
            dismissAccessibilityLabel: "Dismiss",
            onDismiss: {}
        )
        .appPreview()
        .assertSnapshot(testName: "TvManiacBanner_Info")
    }

    func test_TvManiacBanner_WithAction() {
        TvManiacBanner(
            message: "Your Trakt account is full. Upgrade to keep syncing new shows.",
            style: .error,
            dismissAccessibilityLabel: "Dismiss",
            onDismiss: {}
        ) {
            Text("Upgrade")
                .foregroundStyle(BannerStyle.error.backgroundColor)
                .padding(.horizontal, 16)
                .padding(.vertical, 6)
                .background(Color.white, in: Capsule())
        }
        .appPreview()
        .assertSnapshot(testName: "TvManiacBanner_WithAction")
    }
}
