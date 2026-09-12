import Components
import DesignSystem
import Models
import Progress
import SnapshotTestingLib
import SwiftUI
import XCTest

class ProgressScreenTest: SnapshotTestCase {
    func test_ProgressScreen_UpNextSelected() {
        let view = makeScreen(selectedPage: 0, isLoading: false)
        view.assertSnapshot(layout: .defaultDevice, testName: "ProgressScreen_UpNextSelected")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ProgressScreen_UpNextSelected")
    }

    func test_ProgressScreen_CalendarSelected() {
        let view = makeScreen(selectedPage: 1, isLoading: false)
        view.assertSnapshot(layout: .defaultDevice, testName: "ProgressScreen_CalendarSelected")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ProgressScreen_CalendarSelected")
    }

    func test_ProgressScreen_Loading() {
        let view = makeScreen(selectedPage: 0, isLoading: true)
        view.assertSnapshot(layout: .defaultDevice, testName: "ProgressScreen_Loading")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ProgressScreen_Loading")
    }

    private func makeScreen(
        selectedPage: Int,
        isLoading: Bool
    ) -> some View {
        NavigationStack {
            ProgressScreen(
                state: .init(
                    title: "Progress",
                    isLoading: isLoading,
                    selectedPage: selectedPage,
                    upNextTabTitle: "Up Next",
                    calendarTabTitle: "Calendar"
                ),
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
        .appPreview()
    }
}
