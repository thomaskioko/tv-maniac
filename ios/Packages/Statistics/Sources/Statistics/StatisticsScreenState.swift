public extension StatisticsScreen {
    struct State: Equatable {
        public let isLoading: Bool
        public let isLocked: Bool
        public let showEmptyState: Bool
        public let showContent: Bool
        public let showsMarkedWatchedTimes: Bool
        public let totalWatchTime: SwiftWatchTime?
        public let tiles: [SwiftStatisticTile]
        public let mostWatchedShows: [SwiftMostWatchedShowItem]
        public let watchStatusBreakdown: [SwiftWatchStatusItem]
        public let ratingBreakdown: [SwiftRatingBar]
        public let labels: SwiftStatisticsLabels

        public init(
            isLoading: Bool,
            isLocked: Bool = false,
            showEmptyState: Bool = false,
            showContent: Bool = false,
            showsMarkedWatchedTimes: Bool = false,
            totalWatchTime: SwiftWatchTime? = nil,
            tiles: [SwiftStatisticTile] = [],
            mostWatchedShows: [SwiftMostWatchedShowItem] = [],
            watchStatusBreakdown: [SwiftWatchStatusItem] = [],
            ratingBreakdown: [SwiftRatingBar] = [],
            labels: SwiftStatisticsLabels = SwiftStatisticsLabels()
        ) {
            self.isLoading = isLoading
            self.isLocked = isLocked
            self.showEmptyState = showEmptyState
            self.showContent = showContent
            self.showsMarkedWatchedTimes = showsMarkedWatchedTimes
            self.totalWatchTime = totalWatchTime
            self.tiles = tiles
            self.mostWatchedShows = mostWatchedShows
            self.watchStatusBreakdown = watchStatusBreakdown
            self.ratingBreakdown = ratingBreakdown
            self.labels = labels
        }
    }
}
