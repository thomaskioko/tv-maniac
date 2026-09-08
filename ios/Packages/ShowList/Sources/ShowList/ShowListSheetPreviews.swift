import DesignSystem
import SwiftUI

private let previewLabels = ShowListSheet.Labels(
    sheetTitle: "Add to …",
    createListDoneText: "Create",
    createListPlaceholder: "New list name",
    emptyListText: "You don't have any lists yet.",
    listsHeaderText: "Your Lists"
)

private let previewLists: [ShowListItem] = [
    ShowListItem(id: 1, name: "Favorites", showCountText: "12 shows", isShowInList: true),
    ShowListItem(id: 2, name: "Watch Later", showCountText: "5 shows", isShowInList: false),
    ShowListItem(id: 3, name: "Sci-Fi Marathon", showCountText: "23 shows", isShowInList: true),
]

#Preview("Empty") {
    ShowListSheet(state: ShowListSheet.State(isLoading: false, lists: [], labels: previewLabels))
        .appPreview()
        .preferredColorScheme(.dark)
}

#Preview("With Lists") {
    ShowListSheet(state: ShowListSheet.State(isLoading: false, lists: previewLists, labels: previewLabels))
        .appPreview()
        .preferredColorScheme(.dark)
}

#Preview("With Create Field") {
    ShowListSheet(
        state: ShowListSheet.State(
            isLoading: false,
            lists: previewLists,
            showCreateListField: true,
            createListName: "My New List",
            labels: previewLabels
        )
    )
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Creating List") {
    ShowListSheet(
        state: ShowListSheet.State(
            isLoading: false,
            lists: previewLists,
            showCreateListField: true,
            isCreatingList: true,
            createListName: "Sci-Fi Picks",
            labels: previewLabels
        )
    )
    .appPreview()
    .preferredColorScheme(.dark)
}
