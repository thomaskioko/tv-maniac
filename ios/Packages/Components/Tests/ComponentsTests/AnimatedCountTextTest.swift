import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class AnimatedCountTextTest: SnapshotTestCase {
    func test_AnimatedCountText_LargeValue() {
        AnimatedCountText(count: 1250)
            .textStyle(TvManiacTypographyScheme.shared.headlineLarge)
            .padding()
            .appPreview()
            .assertSnapshot(testName: "AnimatedCountText_LargeValue")
    }

    func test_AnimatedCountText_Zero() {
        AnimatedCountText(count: 0)
            .textStyle(TvManiacTypographyScheme.shared.headlineLarge)
            .padding()
            .appPreview()
            .assertSnapshot(testName: "AnimatedCountText_Zero")
    }
}
