public extension StatisticsScreen {
    struct State: Equatable {
        public let isLoading: Bool
        public let isLocked: Bool
        public let showEmptyState: Bool
        public let showContent: Bool
        public let showsMarkedWatchedTimes: Bool
        public let totalWatchTime: SwiftWatchTime?
        public let tiles: [SwiftStatisticTile]
        public let heatMap: SwiftWatchHeatMap?
        public let mostWatchedShows: [SwiftShowRowItem]
        public let highestRatedShows: [SwiftShowRowItem]
        public let watchStatusBreakdown: [SwiftWatchStatusItem]
        public let ratingBreakdown: [SwiftRatingBar]
        public let yearlyActivity: [SwiftActivityBar]
        public let monthlyActivity: [SwiftActivityBar]
        public let weekdayActivity: [SwiftActivityBar]
        public let genreBreakdown: [SwiftGenreSlice]
        public let releaseYears: [SwiftActivityBar]
        public let labels: SwiftStatisticsLabels

        public init(
            isLoading: Bool,
            isLocked: Bool = false,
            showEmptyState: Bool = false,
            showContent: Bool = false,
            showsMarkedWatchedTimes: Bool = false,
            totalWatchTime: SwiftWatchTime? = nil,
            tiles: [SwiftStatisticTile] = [],
            heatMap: SwiftWatchHeatMap? = nil,
            mostWatchedShows: [SwiftShowRowItem] = [],
            highestRatedShows: [SwiftShowRowItem] = [],
            watchStatusBreakdown: [SwiftWatchStatusItem] = [],
            ratingBreakdown: [SwiftRatingBar] = [],
            yearlyActivity: [SwiftActivityBar] = [],
            monthlyActivity: [SwiftActivityBar] = [],
            weekdayActivity: [SwiftActivityBar] = [],
            genreBreakdown: [SwiftGenreSlice] = [],
            releaseYears: [SwiftActivityBar] = [],
            labels: SwiftStatisticsLabels = SwiftStatisticsLabels()
        ) {
            self.isLoading = isLoading
            self.isLocked = isLocked
            self.showEmptyState = showEmptyState
            self.showContent = showContent
            self.showsMarkedWatchedTimes = showsMarkedWatchedTimes
            self.totalWatchTime = totalWatchTime
            self.tiles = tiles
            self.heatMap = heatMap
            self.mostWatchedShows = mostWatchedShows
            self.highestRatedShows = highestRatedShows
            self.watchStatusBreakdown = watchStatusBreakdown
            self.ratingBreakdown = ratingBreakdown
            self.yearlyActivity = yearlyActivity
            self.monthlyActivity = monthlyActivity
            self.weekdayActivity = weekdayActivity
            self.genreBreakdown = genreBreakdown
            self.releaseYears = releaseYears
            self.labels = labels
        }
    }
}
