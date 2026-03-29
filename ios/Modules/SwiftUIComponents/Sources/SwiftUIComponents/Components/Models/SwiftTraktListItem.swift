import Foundation

public struct SwiftTraktListItem: Identifiable, Equatable {
    public var id: Int64 {
        listId
    }

    public let listId: Int64
    public let slug: String
    public let name: String
    public let description: String?
    public let itemCount: Int64
    public let isShowInList: Bool

    public init(
        listId: Int64,
        slug: String,
        name: String,
        description: String?,
        itemCount: Int64,
        isShowInList: Bool
    ) {
        self.listId = listId
        self.slug = slug
        self.name = name
        self.description = description
        self.itemCount = itemCount
        self.isShowInList = isShowInList
    }
}
