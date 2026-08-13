import TvManiacTestTags

enum FixtureData {
    static let breakingBadId: Int64 = 1396
    static let searchQuery = "Breaking Bad"
}

enum DiscoverRow {
    case trending
    case upcoming
    case topRated

    var key: String {
        switch self {
        case .trending: DiscoverTestTags.shared.ROW_KEY_TRENDING
        case .upcoming: DiscoverTestTags.shared.ROW_KEY_UPCOMING
        case .topRated: DiscoverTestTags.shared.ROW_KEY_TOP_RATED
        }
    }
}
