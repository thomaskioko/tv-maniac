import SnapshotTestingLib
import SwiftUI
import DesignSystem
import SwiftUIComponents
import XCTest

class AboutSheetTest: SnapshotTestCase {
    func test_AboutSheet_release() {
        renderAboutSheet(isDebug: false)
            .assertSnapshot(layout: .defaultDevice, testName: "AboutSheet_release")
    }

    func test_AboutSheet_debug() {
        renderAboutSheet(isDebug: true)
            .assertSnapshot(layout: .defaultDevice, testName: "AboutSheet_debug")
    }

    private func renderAboutSheet(isDebug: Bool) -> some View {
        AboutSheet(
            appName: "TvManiac",
            versionText: "Version 1.0.0",
            aboutTitle: "About",
            aboutDescription: "TvManiac is a TV show tracking app built with Kotlin Multiplatform and SwiftUI.",
            sourceCodeLabel: "Source Code",
            sourceCodeAction: "GitHub",
            apiDisclaimer: "This product uses the TMDB API but is not endorsed or certified by TMDB.",
            icon: TvManiacAppIcon.image(isDebug: isDebug),
            onVersionTap: {},
            onSourceCodeTap: {}
        )
        .appPreview()
    }
}
