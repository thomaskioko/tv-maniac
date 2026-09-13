import TvManiac

extension ShowListState {
    func toSwift() -> ShowListSheet.State {
        ShowListSheet.State(
            isLoading: isLoading,
            lists: lists.map { $0.toSwift() },
            showCreateListField: showCreateListField,
            isCreatingList: isCreatingList,
            createListName: createListName,
            labels: labels.toSwift()
        )
    }
}

extension UserListModel {
    func toSwift() -> ShowListItem {
        ShowListItem(
            id: id,
            name: name,
            showCountText: showCountText,
            isShowInList: isShowInList,
            isToggling: isToggling
        )
    }
}

extension ShowListCopy {
    func toSwift() -> ShowListSheet.Labels {
        ShowListSheet.Labels(
            sheetTitle: sheetTitle,
            createListDoneText: createListDoneText,
            createListPlaceholder: createListPlaceholder,
            emptyListText: emptyListText,
            listsHeaderText: listsHeaderText
        )
    }
}
