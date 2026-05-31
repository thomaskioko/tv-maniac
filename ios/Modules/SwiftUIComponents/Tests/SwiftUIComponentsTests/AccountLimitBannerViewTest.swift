import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class AccountLimitBannerViewTest: SnapshotTestCase {
    func test_AccountLimitBannerView() {
        AccountLimitBannerView(
            message: "Your Trakt account is full. Upgrade to keep syncing new shows.",
            upgradeTitle: "Upgrade",
            dismissAccessibilityLabel: "Dismiss",
            onUpgrade: {},
            onDismiss: {}
        )
        .appPreview()
        .assertSnapshot(testName: "AccountLimitBannerView")
    }
}
