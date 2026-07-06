import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class PremiumOverlayModifierTest: SnapshotTestCase {
    func test_PremiumOverlay_Locked() {
        sampleCard
            .premiumOverlay(isLocked: true, accessibilityLabel: "Locked")
            .padding()
            .appPreview()
            .assertSnapshot(testName: "PremiumOverlay_Locked")
    }

    func test_PremiumOverlay_Unlocked() {
        sampleCard
            .premiumOverlay(isLocked: false)
            .padding()
            .appPreview()
            .assertSnapshot(testName: "PremiumOverlay_Unlocked")
    }

    func test_PremiumOverlay_Locked_WithCard() {
        sampleCard
            .frame(width: 320, height: 240)
            .premiumOverlay(
                isLocked: true,
                badgeText: "Premium",
                title: "Calendar is a Premium feature",
                message: "Upgrade to see upcoming episodes for your shows",
                accessibilityLabel: "Locked"
            )
            .padding()
            .appPreview()
            .assertSnapshot(testName: "PremiumOverlay_Locked_WithCard")
    }

    private var sampleCard: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Terminal")
            Text("A retro green-on-black theme")
        }
        .frame(width: 220, height: 120)
        .appCard()
    }
}
