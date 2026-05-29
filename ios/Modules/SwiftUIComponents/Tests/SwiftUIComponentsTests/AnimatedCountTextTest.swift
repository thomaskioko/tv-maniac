import DesignSystem
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class AnimatedCountTextTest: SnapshotTestCase {
    func test_AnimatedCountText_LargeValue() {
        AnimatedCountText(count: 1250)
            .font(.largeTitle)
            .padding()
            .appPreview()
            .assertSnapshot(testName: "AnimatedCountText_LargeValue")
    }

    func test_AnimatedCountText_Zero() {
        AnimatedCountText(count: 0)
            .font(.largeTitle)
            .padding()
            .appPreview()
            .assertSnapshot(testName: "AnimatedCountText_Zero")
    }
}
