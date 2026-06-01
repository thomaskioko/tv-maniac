import TvManiac

// MARK: - Trakt List Mapping

public extension TvManiac.TraktListModel {
    func toSwift() -> SwiftTraktListItem {
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
