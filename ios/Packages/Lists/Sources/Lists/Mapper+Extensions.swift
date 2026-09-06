import Components
import Models
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

extension ListShow {
    func toSwift() -> ShowPosterImage {
        .init(showId: tmdbId, title: title, posterUrl: posterUrl)
    }
}

extension RemoveConfirmation {
    func toSwift() -> ListDetailScreen.RemoveConfirmation {
        .init(
            tmdbId: tmdbId,
            title: title,
            message: message,
            confirmLabel: confirmLabel
        )
    }
}
