import Components
import TvManiac

extension ListsState {
    func toState() -> ListsScreen.State {
        ListsScreen.State(
            title: title,
            isLoading: isLoading,
            emptyMessage: emptyMessage,
            errorMessage: errorMessage,
            lists: lists.map { $0.toSwift() }
        )
    }
}

extension UserListItem {
    func toSwift() -> ListCollageItem {
        .init(
            id: id,
            name: name,
            itemCountLabel: itemCountLabel,
            posterUrls: posterUrls
        )
    }
}
