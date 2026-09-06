import TvManiac

public extension TvManiac.UserListModel {
    func toSwift() -> SwiftListItem {
        .init(
            listId: id,
            slug: slug,
            name: name,
            description: description_,
            showCountText: showCountText,
            isShowInList: isShowInList
        )
    }
}
