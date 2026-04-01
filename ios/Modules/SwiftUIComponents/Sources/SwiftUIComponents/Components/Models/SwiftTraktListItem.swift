import Foundation

public struct SwiftTraktListItem: Identifiable, Equatable {
    public var id: Int64 {
        listId
    }

    public let listId: Int64
    public let slug: String
    public let name: String
    public let description: String?
    public let showCountText: String
    public let isShowInList: Bool

    public init(
        listId: Int64,
        slug: String,
        name: String,
        description: String?,
        showCountText: String,
        isShowInList: Bool
    ) {
        self.listId = listId
        self.slug = slug
        self.name = name
        self.description = description
        self.showCountText = showCountText
        self.isShowInList = isShowInList
    }
}
