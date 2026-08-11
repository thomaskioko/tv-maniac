public enum SwiftListStyle: CaseIterable, Identifiable, Equatable {
    case grid
    case list
    case compact
    case detailed

    public var id: Self {
        self
    }

    public var isPremium: Bool {
        switch self {
        case .grid, .list: false
        case .compact, .detailed: true
        }
    }
}
