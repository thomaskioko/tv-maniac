import DesignSystem
@testable import ShowList
import SnapshotTestingLib
import SwiftUI
import XCTest

class ShowListSheetTest: SnapshotTestCase {
    private let sampleLabels = ShowListSheet.Labels(
        sheetTitle: "Add to …",
        createListDoneText: "Create",
        createListPlaceholder: "New list name",
        emptyListText: "You don't have any lists yet.",
        listsHeaderText: "Your Lists"
    )

    private var sampleLists: [ShowListItem] {
        [
            ShowListItem(id: 1, name: "Favorites", showCountText: "12 shows", isShowInList: true),
            ShowListItem(id: 2, name: "Watch Later", showCountText: "5 shows", isShowInList: false),
            ShowListItem(id: 3, name: "Sci-Fi Marathon", showCountText: "23 shows", isShowInList: true),
        ]
    }

    func test_ShowListSheet_Empty() {
        makeSheet(
            state: ShowListSheet.State(
                isLoading: false,
                lists: [],
                labels: sampleLabels
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "ShowListSheet_Empty")
    }

    func test_ShowListSheet_WithLists() {
        makeSheet(
            state: ShowListSheet.State(
                isLoading: false,
                lists: sampleLists,
                labels: sampleLabels
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "ShowListSheet_WithLists")
    }

    func test_ShowListSheet_WithCreateField() {
        makeSheet(
            state: ShowListSheet.State(
                isLoading: false,
                lists: sampleLists,
                showCreateListField: true,
                createListName: "My New List",
                labels: sampleLabels
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "ShowListSheet_WithCreateField")
    }

    func test_ShowListSheet_CreatingList() {
        makeSheet(
            state: ShowListSheet.State(
                isLoading: false,
                lists: sampleLists,
                showCreateListField: true,
                isCreatingList: true,
                createListName: "Sci-Fi Picks",
                labels: sampleLabels
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "ShowListSheet_CreatingList")
    }

    private func makeSheet(state: ShowListSheet.State) -> some View {
        ShowListSheet(state: state)
            .appPreview()
    }
}
