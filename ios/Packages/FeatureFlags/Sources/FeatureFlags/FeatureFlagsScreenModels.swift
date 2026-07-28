public extension FeatureFlagsScreen {
    struct State: Equatable {
        public let title: String
        public let searchQuery: String
        public let searchPlaceholder: String
        public let resetAllTitle: String
        public let resetAllSubtitle: String
        public let forceRefreshTitle: String
        public let forceRefreshSubtitle: String
        public let resetButtonLabel: String
        public let emptyMessage: String
        public let moreActionsLabel: String
        public let groupByTypeLabel: String
        public let noGroupingLabel: String
        public let sortAscendingLabel: String
        public let sortDescendingLabel: String
        public let sortOptions: [SortOption]
        public let activeSortId: String
        public let ascending: Bool
        public let groupByType: Bool
        public let items: [Item]

        public init(
            title: String,
            searchQuery: String,
            searchPlaceholder: String,
            resetAllTitle: String,
            resetAllSubtitle: String,
            forceRefreshTitle: String,
            forceRefreshSubtitle: String,
            resetButtonLabel: String,
            emptyMessage: String,
            moreActionsLabel: String,
            groupByTypeLabel: String,
            noGroupingLabel: String,
            sortAscendingLabel: String,
            sortDescendingLabel: String,
            sortOptions: [SortOption],
            activeSortId: String,
            ascending: Bool,
            groupByType: Bool,
            items: [Item]
        ) {
            self.title = title
            self.searchQuery = searchQuery
            self.searchPlaceholder = searchPlaceholder
            self.resetAllTitle = resetAllTitle
            self.resetAllSubtitle = resetAllSubtitle
            self.forceRefreshTitle = forceRefreshTitle
            self.forceRefreshSubtitle = forceRefreshSubtitle
            self.resetButtonLabel = resetButtonLabel
            self.emptyMessage = emptyMessage
            self.moreActionsLabel = moreActionsLabel
            self.groupByTypeLabel = groupByTypeLabel
            self.noGroupingLabel = noGroupingLabel
            self.sortAscendingLabel = sortAscendingLabel
            self.sortDescendingLabel = sortDescendingLabel
            self.sortOptions = sortOptions
            self.activeSortId = activeSortId
            self.ascending = ascending
            self.groupByType = groupByType
            self.items = items
        }
    }

    struct Item: Identifiable, Equatable {
        public let id: String
        public let title: String
        public let description: String
        public let source: String
        public let isOn: Bool
        public let isLocal: Bool

        public init(
            id: String,
            title: String,
            description: String,
            source: String,
            isOn: Bool,
            isLocal: Bool
        ) {
            self.id = id
            self.title = title
            self.description = description
            self.source = source
            self.isOn = isOn
            self.isLocal = isLocal
        }
    }

    struct SortOption: Identifiable, Equatable {
        public let id: String
        public let label: String

        public init(id: String, label: String) {
            self.id = id
            self.label = label
        }
    }
}
