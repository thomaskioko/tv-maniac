import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class AboutSheetTest: SnapshotTestCase {
    func test_AboutSheet() {
        AboutSheet(
            appName: "TvManiac",
            versionText: "Version 1.0.0",
            aboutTitle: "About",
            aboutDescription: "TvManiac is a TV show tracking app built with Kotlin Multiplatform and SwiftUI.",
            sourceCodeLabel: "Source Code",
            sourceCodeAction: "GitHub",
            apiDisclaimer: "This product uses the TMDB API but is not endorsed or certified by TMDB.",
            icon: TvManiacAppIcon.image,
            onVersionTap: {},
            onSourceCodeTap: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "AboutSheet")
    }
}
