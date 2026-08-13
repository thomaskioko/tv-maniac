import Models

public struct LayoutMenuCopy: Equatable {
    public let grid: String
    public let list: String
    public let compact: String
    public let detailed: String
    public let premiumSectionTitle: String
    public let lockedAccessibilitySuffix: String
    public let lockedHint: String
    public let upgradeActionName: String

    public init(
        grid: String,
        list: String,
        compact: String,
        detailed: String,
        premiumSectionTitle: String,
        lockedAccessibilitySuffix: String,
        lockedHint: String,
        upgradeActionName: String
    ) {
        self.grid = grid
        self.list = list
        self.compact = compact
        self.detailed = detailed
        self.premiumSectionTitle = premiumSectionTitle
        self.lockedAccessibilitySuffix = lockedAccessibilitySuffix
        self.lockedHint = lockedHint
        self.upgradeActionName = upgradeActionName
    }

    public func label(for layout: SwiftListStyle) -> String {
        switch layout {
        case .grid: grid
        case .list: list
        case .compact: compact
        case .detailed: detailed
        }
    }
}
