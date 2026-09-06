import Components

public extension ListsScreen {
    struct State: Equatable {
        public let title: String
        public let isLoading: Bool
        public let emptyMessage: String
        public let errorMessage: String?
        public let lists: [ListCollageItem]

        public init(
            title: String,
            isLoading: Bool = true,
            emptyMessage: String = "",
            errorMessage: String? = nil,
            lists: [ListCollageItem] = []
        ) {
            self.title = title
            self.isLoading = isLoading
            self.emptyMessage = emptyMessage
            self.errorMessage = errorMessage
            self.lists = lists
        }
    }
}
