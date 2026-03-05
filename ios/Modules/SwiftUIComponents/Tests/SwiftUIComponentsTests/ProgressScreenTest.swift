import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ProgressScreenTest: SnapshotTestCase {
    func test_ProgressScreen_UpNextSelected() {
        makeScreen(selectedPage: 0, isLoading: false)
            .assertSnapshot(layout: .defaultDevice, testName: "ProgressScreen_UpNextSelected")
    }

    func test_ProgressScreen_CalendarSelected() {
        makeScreen(selectedPage: 1, isLoading: false)
            .assertSnapshot(layout: .defaultDevice, testName: "ProgressScreen_CalendarSelected")
    }

    func test_ProgressScreen_Loading() {
        makeScreen(selectedPage: 0, isLoading: true)
            .assertSnapshot(layout: .defaultDevice, testName: "ProgressScreen_Loading")
    }

    private func makeScreen(
        selectedPage: Int,
        isLoading: Bool
    ) -> some View {
        NavigationStack {
            ProgressScreen(
                title: "Progress",
                isLoading: isLoading,
                selectedPage: selectedPage,
                upNextTabTitle: "Up Next",
                calendarTabTitle: "Calendar",
                onPageChanged: { _ in },
                upNextContent: {
                    Text("Up Next Content")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                },
                calendarContent: {
                    Text("Calendar Content")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
            )
        }
        .themedPreview()
    }
}
